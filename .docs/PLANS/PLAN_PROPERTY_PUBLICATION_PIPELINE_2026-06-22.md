# Plan: Pipeline de Publicación de Propiedad

## Objetivo

Implementar el flujo completo de creación y publicación de una propiedad, desde que el lessor envía el draft hasta que queda persistida en PostgreSQL. El flujo incluye subida de media directamente a Cloudinary desde el cliente, dos validaciones externas asíncronas, y notificaciones al cliente vía FCM + SSE. RabbitMQ es el backbone de toda la comunicación asíncrona.

## Alcance

**Incluido:**
- Eliminación completa de DynamoDB del feature `draft`
- Redis como única fuente de verdad del draft (stage temporal)
- Cloudinary signed upload en lugar de S3 presigned URLs
- RabbitMQ para desacoplar media notifications, validaciones y notificaciones
- Validación 1: moderación de contenido +18 (asíncrona, horas)
- Validación 2: detección de anomalías (asíncrona, minutos)
- Notificaciones al cliente: FCM (push) + SSE (streaming en tiempo real)
- Persistencia final en PostgreSQL al aprobar
- Copia a Firestore/DocumentDB cuando la validación 2 rechaza

**Excluido:**
- Implementación interna de los servicios de validación (son externos)
- Definición del schema de Firestore (tentativo, se define cuando se confirme el proveedor)
- Integración con APNs (iOS) — solo FCM en esta fase

---

## Estados del Draft

El draft en Redis transita por estos estados. Son la fuente de verdad del progreso:

```
PENDING_MEDIA
    │
    ▼ (todos los archivos confirmados via queue)
MEDIA_COMPLETE
    │
    ▼ (se publica trabajo a validación 1)
CONTENT_VALIDATION_PENDING
    │
    ├─[rechazado]──► CONTENT_REJECTED  → eliminar Redis, notificar cliente (FCM)
    │
    ▼ (aprobado)
CONTENT_APPROVED
    │
    ▼ (se publica trabajo a validación 2)
ANOMALY_VALIDATION_PENDING
    │
    ├─[rechazado]──► ANOMALY_REJECTED  → copiar a Firestore, eliminar Redis, notificar cliente (FCM)
    │
    ▼ (aprobado)
PUBLISHED                              → escribir en PostgreSQL, eliminar Redis, notificar cliente (FCM)
```

**TTL en Redis:** Absoluto. Se configura una sola vez al crear el draft. Si expira, el draft desaparece sin importar el estado en que esté. No se extiende.

**Múltiples drafts por lessor:** Permitidos. La clave Redis es `property:draft:{draftId}`, no `property:draft:{lessorId}`.

---

## Tecnologías Involucradas

| Componente | Tecnología | Rol |
|---|---|---|
| Stage del draft | Redis | Fuente de verdad temporal (con TTL absoluto) |
| Media storage | Cloudinary | Almacenamiento + CDN de imágenes/videos |
| Mensajería asíncrona | RabbitMQ | Backbone de todos los eventos del pipeline |
| Validación 1 | Servicio externo HTTP + queue | Moderación de contenido +18 |
| Validación 2 | Servicio externo HTTP + queue | Detección de anomalías |
| Notificaciones push | FCM (Firebase Cloud Messaging) | Push al dispositivo móvil |
| Notificaciones en tiempo real | SSE (Server-Sent Events) | Stream mientras la app está abierta |
| Persistencia final | PostgreSQL | Propiedades publicadas |
| Drafts rechazados por anomalías | Firestore (Native Mode) | Base de análisis |

---

## Flujo de Datos Completo

### Paso 1 — POST /properties/draft

**Quién:** Cliente (lessor autenticado)  
**Qué recibe el backend:** Datos de la propiedad + manifest de archivos (nombre, tipo MIME, tamaño por archivo)

**Backend:**
1. Valida el manifest (tipos permitidos, límites de cantidad y tamaño)
2. Resuelve `propertyTypeId` y `neighborhoodId` contra PostgreSQL
3. Calcula `pricePerM2`
4. Genera un `draftId` (UUID)
5. Por cada archivo del manifest, genera un **Cloudinary signed upload** (firma HMAC-SHA1 con `timestamp`, `folder`, `public_id`, `api_key` del secret del backend)
6. Construye el objeto `PropertyDraft` con status `PENDING_MEDIA`, `totalFiles`, `uploadedFiles = 0`
7. Guarda en Redis con TTL absoluto

**Responde al cliente:**
```json
{
  "draftId": "uuid",
  "status": "PENDING_MEDIA",
  "expiresAt": "ISO-8601",
  "uploads": [
    {
      "fileKey": "foto_sala",
      "cloudinaryUrl": "https://api.cloudinary.com/v1_1/{cloud}/image/upload",
      "signature": "abc123...",
      "timestamp": 1234567890,
      "apiKey": "...",
      "folder": "drafts/{draftId}",
      "publicId": "drafts/{draftId}/foto_sala"
    }
  ]
}
```

El cliente usa estos parámetros para subir cada archivo **directamente a Cloudinary** sin pasar por este backend.

---

### Paso 2 — Cloudinary notifica cada subida completada

**Mecanismo:** Cloudinary webhook → `POST /internal/media/uploaded` en este backend → el backend publica en RabbitMQ.

**Por qué no procesar el webhook directamente:** Si el procesamiento falla (Redis caído, error en lógica), el evento se pierde. Al publicar en una queue, el mensaje persiste hasta ser consumido con éxito.

**Queue:** `vivia.media.file.uploaded`  
**Payload mínimo:** `{ draftId, fileKey, cloudinaryPublicId, status: "success"|"error" }`

**Consumer `MediaUploadConsumer`:**
1. Lee el draft de Redis
2. Si el draft no existe (TTL expirado) → descarta el mensaje, solicita a Cloudinary eliminar el archivo
3. Incrementa `uploadedFiles` en Redis (operación atómica `HINCRBY`)
4. Si `uploadedFiles == totalFiles` → actualiza status a `MEDIA_COMPLETE` y publica en `vivia.validation.content.submit`

**Por qué el conteo en Redis no rompe el stateless:** El estado vive en Redis, no en memoria de la instancia de la aplicación. Cualquier instancia puede procesar el mensaje y leer/escribir el mismo contador en Redis.

---

### Paso 3 — Validación 1: Moderación de Contenido +18

**Queue de salida:** `vivia.validation.content.submit`  
**Payload:** Draft completo serializado (todos los campos + URLs públicas de Cloudinary por archivo)

**Integración con el servicio externo:**  
Hay dos sub-patrones dependiendo de si el servicio externo soporta RabbitMQ o solo HTTP:

- **Si soporta RabbitMQ:** consume directamente de `vivia.validation.content.submit` y publica su resultado en `vivia.validation.content.result`
- **Si solo soporta HTTP:** un `ContentValidationSubmitter` consume la queue, hace la llamada HTTP al servicio, y el servicio devuelve el resultado vía webhook a `POST /internal/validations/content/result` → el backend publica en `vivia.validation.content.result`

**Queue de entrada de resultado:** `vivia.validation.content.result`  
**Payload:** `{ draftId, approved: true|false, reason?: "..." }`

**Consumer `ContentValidationResultConsumer`:**
- **Si rechazado:**
  1. Actualiza status Redis a `CONTENT_REJECTED`
  2. Elimina archivos de Cloudinary (carpeta `drafts/{draftId}/`)
  3. Publica en `vivia.notification.send` → FCM al lessor
  4. Elimina el draft de Redis
- **Si aprobado:**
  1. Actualiza status Redis a `CONTENT_APPROVED`
  2. Publica en `vivia.validation.anomaly.submit`
  3. Publica en `vivia.notification.send` (opcional: notificar progreso)

---

### Paso 4 — Validación 2: Detección de Anomalías

**Queue de salida:** `vivia.validation.anomaly.submit`  
**Payload:** Draft completo serializado

Mismo patrón de integración que validación 1 (HTTP o RabbitMQ nativo).

**Queue de entrada de resultado:** `vivia.validation.anomaly.result`  
**Payload:** `{ draftId, approved: true|false, reason?: "...", anomalyData?: {...} }`

**Consumer `AnomalyValidationResultConsumer`:**
- **Si rechazado:**
  1. Actualiza status Redis a `ANOMALY_REJECTED`
  2. Guarda copia completa del draft en Firestore/DocumentDB (incluyendo `anomalyData` del resultado)
  3. Publica en `vivia.notification.send` → FCM al lessor
  4. Elimina el draft de Redis
  5. **No elimina archivos de Cloudinary** (quedan para análisis interno)
- **Si aprobado:**
  1. Escribe la propiedad en PostgreSQL (mapeo completo de `PropertyDraft` → `PropertyEntity`, creación de dirección, media, etc.)
  2. Actualiza status Redis a `PUBLISHED`
  3. Publica en `vivia.notification.send` → FCM al lessor
  4. Elimina el draft de Redis

---

### Paso 5 — Notificaciones

#### FCM (Firebase Cloud Messaging)

**Consumer `NotificationConsumer`** consume de `vivia.notification.send`.

**Payload:** `{ userId, title, body, data: { draftId, status, ... } }`

Se dispara en cada estado terminal:
- `CONTENT_REJECTED` → "Tu propiedad no pudo ser publicada (contenido)"
- `ANOMALY_REJECTED` → "Tu propiedad está en revisión manual"
- `PUBLISHED` → "¡Tu propiedad ya está publicada!"

#### SSE (Server-Sent Events)

**Endpoint:** `GET /properties/draft/{draftId}/status/stream`

El cliente se conecta cuando tiene la app abierta y quiere ver el progreso en tiempo real. El backend mantiene la conexión SSE viva y emite un evento cada vez que el status del draft cambia en Redis.

**Mecanismo:** Redis Pub/Sub. Cuando cualquier consumer cambia el status del draft, publica en el canal `draft:status:{draftId}`. El SSE controller está suscrito a ese canal y re-emite el evento al cliente HTTP conectado.

**Formato del evento SSE:**
```
event: status_update
data: {"draftId":"...","status":"CONTENT_APPROVED","updatedAt":"..."}
```

La combinación FCM + SSE garantiza que:
- Si la app está abierta: el usuario ve el progreso en tiempo real (SSE)
- Si la app está en background o cerrada: recibe push notification (FCM)

---

## Exchanges y Queues de RabbitMQ

```
Exchange: vivia.properties (topic)
│
├── vivia.media.file.uploaded          ← Cloudinary webhook → backend → aquí
├── vivia.validation.content.submit    ← cuando media está completa
├── vivia.validation.content.result    ← resultado de validación 1
├── vivia.validation.anomaly.submit    ← cuando validación 1 aprueba
├── vivia.validation.anomaly.result    ← resultado de validación 2
└── vivia.notification.send            ← para despachar FCM
```

Todas las queues con `durable: true` para sobrevivir reinicios de RabbitMQ.  
Dead-letter queue: `vivia.dlq` para mensajes que fallan repetidamente.

---

## Redis: Estructura de Datos del Draft

**Clave:** `property:draft:{draftId}`  
**Tipo:** Hash (facilita actualizar campos individuales atómicamente)  
**TTL:** Absoluto, configurado en `application.properties` (`vivia.property.draft.ttl-hours`)

**Campos relevantes:**

| Campo | Tipo | Descripción |
|---|---|---|
| `status` | String | Estado actual del draft |
| `totalFiles` | int | Archivos esperados |
| `uploadedFiles` | int | Archivos confirmados por Cloudinary |
| `lessorId` | UUID | Para notificaciones FCM |
| `draftJson` | String (JSON) | Serialización completa del `PropertyDraft` |

El `draftJson` contiene todos los datos de la propiedad para poder enviárselos a los servicios de validación sin consultar otra fuente.

---

## Cambios por Capa

### Dominio (`domain/entities/`)
- **`PropertyDraft`**: Sin cambios estructurales. Se eliminan referencias a DynamoDB en capas de infraestructura.
- **`PropertyDraftMedia`**: Agregar campo `cloudinaryPublicId` (reemplaza `s3Key`).

### Infraestructura (`data/`)
- **Eliminar:** `PropertyDraftDynamoItem`, `PropertyDraftDynamoRepository`, `PropertyDraftRepositoryAdapter` (DynamoDB)
- **Eliminar:** `IS3PresignService`, `S3PresignServiceImpl` (reemplazado por Cloudinary)
- **Agregar:** `CloudinarySignedUploadService` — genera firmas para upload directo
- **Agregar:** `PropertyDraftRedisRepository` — operaciones Redis tipadas (CRUD + contador atómico)
- **Mantener:** `RedisTemplate` ya configurado

### Mensajería (`messaging/`)

Nueva capa bajo `features/properties/draft/messaging/`:

- **Publishers:**
  - `MediaUploadEventPublisher` — publica en `vivia.media.file.uploaded` (llamado desde el webhook HTTP)
  - `ContentValidationPublisher` — publica en `vivia.validation.content.submit`
  - `AnomalyValidationPublisher` — publica en `vivia.validation.anomaly.submit`
  - `NotificationPublisher` — publica en `vivia.notification.send`

- **Consumers:**
  - `MediaUploadConsumer` — consume `vivia.media.file.uploaded`, actualiza contador Redis
  - `ContentValidationResultConsumer` — consume `vivia.validation.content.result`
  - `AnomalyValidationResultConsumer` — consume `vivia.validation.anomaly.result`
  - `NotificationConsumer` — consume `vivia.notification.send`, llama FCM

### Servicios (`services/`)
- **Modificar:** `PropertyDraftServiceImpl` — eliminar DynamoDB, generar firma Cloudinary en lugar de S3 presigned URL
- **Agregar:** `ContentValidationService` — construye y envía el payload a validación 1
- **Agregar:** `AnomalyValidationService` — construye y envía el payload a validación 2
- **Agregar:** `PropertyPublicationService` — mapea `PropertyDraft` → `PropertyEntity` y persiste en PostgreSQL
- **Agregar:** `AnalysisStorageService` — persiste draft rechazado en Firestore

### Controladores (`controllers/`)
- **Mantener:** `POST /properties/draft` — ajustar respuesta para Cloudinary en lugar de S3
- **Agregar:** `POST /internal/media/uploaded` — webhook de Cloudinary (no autenticado por JWT, validado por signature de Cloudinary)
- **Agregar:** `POST /internal/validations/content/result` — webhook del servicio de validación 1 (si es HTTP)
- **Agregar:** `POST /internal/validations/anomaly/result` — webhook del servicio de validación 2 (si es HTTP)
- **Agregar:** `GET /properties/draft/{draftId}/status/stream` — SSE endpoint

---

## Dependencias Externas Nuevas

| Dependencia | Uso |
|---|---|
| `cloudinary-java` | SDK de Cloudinary para generar firmas |
| `spring-boot-starter-amqp` | RabbitMQ (AMQP) |
| `firebase-admin` | Envío de push notifications FCM |
| `firebase-admin` (ya incluido) | Firestore Native Mode vía Admin SDK (mismo SDK que FCM) |
| WebFlux o `SseEmitter` de Spring MVC | SSE endpoint |

---

## Dependencias entre Componentes

```
PropertyDraftController
    └── PropertyDraftServiceImpl
            ├── CloudinarySignedUploadService
            └── PropertyDraftRedisRepository

WebhookController (media/uploaded)
    └── MediaUploadEventPublisher → [queue] → MediaUploadConsumer
            └── PropertyDraftRedisRepository
                    └── [si completo] ContentValidationPublisher

ContentValidationResultConsumer
    ├── PropertyDraftRedisRepository
    ├── CloudinaryCleanupService (si rechazado)
    ├── AnomalyValidationPublisher (si aprobado)
    └── NotificationPublisher

AnomalyValidationResultConsumer
    ├── PropertyPublicationService (si aprobado) → PostgreSQL
    ├── AnalysisStorageService (si rechazado) → Firestore
    ├── PropertyDraftRedisRepository
    └── NotificationPublisher

NotificationConsumer
    └── FcmService → FCM

SseController
    └── Redis Pub/Sub (suscrito a canal draft:status:{draftId})
```

---

## Pasos de Implementación (orden sugerido)

1. **Eliminar DynamoDB**: borrar `PropertyDraftDynamoItem`, `PropertyDraftDynamoRepository`, `PropertyDraftRepositoryAdapter`, dependencia de AWS DynamoDB en `pom.xml`
2. **Refactorizar Redis**: convertir el draft a Redis Hash tipado en lugar de serializar el objeto completo como `Object`. Implementar `PropertyDraftRedisRepository`.
3. **Integrar Cloudinary**: reemplazar `IS3PresignService` con `CloudinarySignedUploadService`. Actualizar `PropertyDraftServiceImpl`.
4. **Configurar RabbitMQ**: declarar exchanges, queues y bindings via `@Configuration`. Agregar dependencia AMQP.
5. **Webhook de Cloudinary**: implementar `POST /internal/media/uploaded` + `MediaUploadEventPublisher` + `MediaUploadConsumer`.
6. **Validación 1**: `ContentValidationPublisher` + integración HTTP o queue con el servicio externo + `ContentValidationResultConsumer`.
7. **Validación 2**: `AnomalyValidationPublisher` + integración + `AnomalyValidationResultConsumer` + `PropertyPublicationService` + `AnalysisStorageService`.
8. **FCM**: `NotificationConsumer` + `FcmService` + configuración Firebase Admin SDK.
9. **SSE**: `GET /properties/draft/{draftId}/status/stream` con Redis Pub/Sub.
10. **Testing de integración**: flujo completo con RabbitMQ y Redis en contenedores locales.
