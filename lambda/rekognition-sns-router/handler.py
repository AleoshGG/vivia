"""
Lambda dedicada al flujo de media-session (medios adicionales en propiedades publicadas).

Disparada por SNS cuando Rekognition termina un job de video cuyo JobTag tiene
el formato "media-session:<sessionId>". Llama a:
  POST /internal/property-media/validation/result  { sessionId, approved, reason }

Variables de entorno requeridas:
  WEBHOOK_URL          — URL completa del endpoint (ej. https://api.vivia.aleosh.online/internal/property-media/validation/result)
  INTERNAL_API_KEY     — clave compartida para X-Internal-Api-Key
  CONFIDENCE_THRESHOLD — umbral de confianza de Rekognition (default 75.0)
"""

import json
import boto3
import urllib.request
import urllib.error
import os
import uuid

WEBHOOK_URL = os.environ["WEBHOOK_URL"]
INTERNAL_API_KEY = os.environ["INTERNAL_API_KEY"]
CONFIDENCE_THRESHOLD = float(os.environ.get("CONFIDENCE_THRESHOLD", "75.0"))

MEDIA_SESSION_PREFIX = "media-session:"

rekognition = boto3.client("rekognition")


# ── Punto de entrada ──────────────────────────────────────────────────────────

def lambda_handler(event, context):
    records = event.get("Records")
    if not records:
        print("[ERROR] El evento no contiene 'Records'. Evento ignorado.")
        return

    for record in records:
        _process_record(record)


# ── Procesamiento por record ──────────────────────────────────────────────────

def _process_record(record):
    # 1. Parsear el mensaje SNS
    try:
        raw_message = record["Sns"]["Message"]
        sns_message = json.loads(raw_message)
    except (KeyError, TypeError):
        print(f"[ERROR] Estructura SNS inesperada — no se encontró Sns.Message. Record: {record}")
        return
    except json.JSONDecodeError as e:
        print(f"[ERROR] El cuerpo del mensaje SNS no es JSON válido: {e}. Contenido: {record.get('Sns', {}).get('Message')}")
        return

    job_id  = sns_message.get("JobId")
    status  = sns_message.get("Status")
    job_tag = sns_message.get("JobTag", "")

    # 2. Validar campos mínimos
    if not job_id:
        print(f"[ERROR] Mensaje SNS sin JobId — ignorando. Mensaje: {sns_message}")
        return

    if not job_tag:
        print(f"[ERROR] JobTag vacío — no se puede identificar la sesión. jobId={job_id}")
        return

    # 3. Verificar que el JobTag pertenece a este flujo
    if not job_tag.startswith(MEDIA_SESSION_PREFIX):
        print(f"[SKIP] JobTag '{job_tag}' no pertenece al flujo media-session — ignorando. jobId={job_id}")
        return

    session_id = job_tag[len(MEDIA_SESSION_PREFIX):]

    # 4. Validar que session_id sea un UUID válido
    try:
        uuid.UUID(str(session_id))
    except ValueError:
        print(f"[ERROR] sessionId no es un UUID válido: '{session_id}' — ignorando. jobId={job_id}")
        return

    # 5. Verificar que Rekognition completó el job correctamente
    if status != "SUCCEEDED":
        reason = _rekognition_status_reason(status, job_id)
        print(f"[WARN] Job de Rekognition no exitoso: {reason}")
        call_webhook(session_id, False, reason)
        return

    # 6. Obtener resultados de moderación
    try:
        response = rekognition.get_content_moderation(JobId=job_id)
    except rekognition.exceptions.InvalidJobIdException:
        reason = f"El job de análisis de video no existe o ya expiró (jobId={job_id})"
        print(f"[ERROR] {reason}")
        call_webhook(session_id, False, reason)
        return
    except rekognition.exceptions.AccessDeniedException:
        reason = f"Sin permisos para obtener resultados de Rekognition (jobId={job_id})"
        print(f"[ERROR] {reason}")
        call_webhook(session_id, False, reason)
        return
    except Exception as e:
        reason = f"Error inesperado al obtener resultados de Rekognition (jobId={job_id}): {type(e).__name__}"
        print(f"[ERROR] {reason} — detalle: {e}")
        call_webhook(session_id, False, reason)
        return

    # 7. Evaluar labels de moderación
    try:
        labels = response.get("ModerationLabels", [])
        flagged = [
            l for l in labels
            if l.get("ModerationLabel", {}).get("Confidence", 0) >= CONFIDENCE_THRESHOLD
        ]
    except Exception as e:
        reason = f"Error al procesar los labels de moderación de Rekognition (jobId={job_id}): {type(e).__name__}"
        print(f"[ERROR] {reason} — detalle: {e}")
        call_webhook(session_id, False, reason)
        return

    if flagged:
        names = ", ".join(sorted(set(
            l.get("ModerationLabel", {}).get("Name", "Desconocido") for l in flagged
        )))
        reason = f"Video con contenido inapropiado detectado: {names}"
        print(f"[WARN] sessionId={session_id} rechazado — {reason}")
        call_webhook(session_id, False, reason)
    else:
        print(f"[OK] sessionId={session_id} — video aprobado, sin contenido inapropiado.")
        call_webhook(session_id, True, None)


# ── Webhook ───────────────────────────────────────────────────────────────────

def call_webhook(session_id, approved, reason):
    body = json.dumps({
        "sessionId": session_id,
        "approved": approved,
        "reason": reason
    }).encode("utf-8")

    req = urllib.request.Request(
        WEBHOOK_URL,
        data=body,
        headers={
            "Content-Type": "application/json",
            "X-Internal-Api-Key": INTERNAL_API_KEY
        },
        method="POST"
    )

    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            print(f"[OK] Webhook respondió {resp.status} — sessionId={session_id}, approved={approved}")
    except urllib.error.HTTPError as e:
        body_text = e.read().decode("utf-8", errors="replace")
        print(f"[ERROR] Webhook rechazó la solicitud: HTTP {e.code} — sessionId={session_id}. Respuesta: {body_text}")
        raise
    except urllib.error.URLError as e:
        print(f"[ERROR] No se pudo conectar al webhook — sessionId={session_id}: {e.reason}")
        raise
    except TimeoutError:
        print(f"[ERROR] Timeout al llamar al webhook — sessionId={session_id}")
        raise


# ── Helpers ───────────────────────────────────────────────────────────────────

def _rekognition_status_reason(status, job_id):
    mensajes = {
        "FAILED":      f"Rekognition no pudo procesar el video (jobId={job_id}). El archivo puede estar corrupto, en un formato no soportado, o ser demasiado largo.",
        "IN_PROGRESS": f"El job de Rekognition todavía está en progreso (jobId={job_id}). Posible notificación duplicada de SNS.",
    }
    return mensajes.get(status, f"Estado desconocido de Rekognition: '{status}' (jobId={job_id})")
