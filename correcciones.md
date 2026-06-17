# Bitácora de Correcciones - Proyecto Vivia

Este documento detalla los problemas encontrados durante la fase de estabilización del entorno de desarrollo y las soluciones aplicadas para lograr que la aplicación Spring Boot (v3.4.3) inicie correctamente.

## 1. Mapeo de Entidades y Dominios
- **Problema:** Error en `CredentialMapper` por incompatibilidad de tipos entre `UUID` (Dominio) y `UserEntity` (Capa de datos).
- **Solución:** Se ajustó el método `toEntity` para instanciar un `UserEntity` de referencia utilizando únicamente el ID del dominio, permitiendo que JPA maneje la relación de llave foránea sin cargar el objeto completo.

## 2. Gestión de Dependencias (Maven)
- **Problema:** Fallo al encontrar clases core (como `org.springframework.boot.thread.Threading`) y colisiones de versiones.
- **Causa:** Uso de versiones `RELEASE` o versiones fijas incompatibles con el `parent` de Spring Boot 3.4.3 en el archivo `pom.xml`.
- **Solución:** Se eliminaron las etiquetas `<version>` de los starters de Spring Boot (Security, Redis, Web) y Flyway para que el BOM (Bill of Materials) del parent gestione las versiones compatibles automáticamente.

## 3. Incompatibilidad con Springdoc OpenAPI
- **Problema:** Error `NoClassDefFoundError: org/springframework/boot/web/error/ErrorPageRegistrar`.
- **Causa:** La versión `3.0.1` de Springdoc no era compatible con los cambios internos de Spring Boot 3.4.
- **Solución:** Se bajó la versión de `springdoc-openapi-starter-webmvc-ui` a la **`2.8.5`**, la cual es estable para la versión actual de Spring. Además, se habilitó `spring.main.allow-bean-definition-overriding=true` para resolver colisiones de beans de resolución de errores.

## 4. Validación de Esquema JPA (Hibernate)
- **Problema:** Error `wrong column type encountered` en `latitude` y `longitude` de la tabla `lessees`.
- **Causa:** La base de datos usaba `DECIMAL(12, 9)` pero Java usaba `Double`, lo que provocaba que Hibernate esperara un tipo `FLOAT`.
- **Solución:** Se cambió el tipo de dato en `LesseeEntity` y su Mapper de `Double` a **`BigDecimal`**, especificando `precision = 12` y `scale = 9` en la anotación `@Column`.

## 5. Migraciones de Base de Datos (Flyway)
- **Problema:** Error `missing table [refresh_tokens]` y errores de tipo de dato en el ID.
- **Solución:**
    1. Se creó el script `V4__create_refresh_tokens_table.sql`.
    2. Se añadió `@JdbcTypeCode(Types.VARCHAR)` en `RefreshTokenEntity` para que el campo `UUID` de Java sea compatible con el campo `VARCHAR(50)` definido en los scripts de migración.

## 6. Variables de Entorno y Configuración de AWS
- **Problema:** La aplicación no arrancaba porque no encontraba las variables de entorno de AWS (`AWS_ACCESS_KEY_ID`).
- **Solución:** Se añadieron valores por defecto (fallbacks) en `application.properties` (ej. `${VAR:default}`) para permitir el arranque en entornos de desarrollo locales sin configuraciones externas obligatorias.

## 7. Configuración de Firebase robusta
- **Problema:** El arranque se detenía si no existía el archivo JSON de credenciales de Firebase.
- **Solución:** Se modificó `FirebaseConfig.java` para validar la existencia del recurso. Si el archivo no existe, la aplicación imprime un aviso pero continúa el arranque devolviendo un bean nulo, evitando el bloqueo del desarrollo.

## 8. Servicios de Infraestructura (Redis)
- **Problema:** Error de conexión rehusada en el puerto 6379 (Redis).
- **Solución:** Para facilitar el desarrollo sin dependencias externas obligatorias, se excluyeron las clases `RedisAutoConfiguration` y `RedisRepositoriesAutoConfiguration` en la anotación `@SpringBootApplication` de la clase principal.

## 9. Estabilización de Servicios en Refactorización
- **Problema:** Beans no encontrados (`ILesseeService`, `IPropertyService`) debido a que las implementaciones eran abstractas o contenían errores de compilación por cambios en el esquema.
- **Solución:** Se crearon implementaciones concretas "limpias" con métodos que lanzan `RuntimeException("Not implemented yet")`. Esto permite que Spring instancie los controladores y la API sea funcional mientras se termina de escribir la lógica de negocio interna.
