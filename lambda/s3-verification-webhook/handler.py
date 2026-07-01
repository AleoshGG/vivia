import json
import os
import urllib.error
import urllib.parse
import urllib.request

VIVIA_API_URL = os.environ["VIVIA_API_URL"]      # e.g. https://api.vivia.aleosh.online
INTERNAL_API_KEY = os.environ["VIVIA_INTERNAL_API_KEY"]
WEBHOOK_PATH = "/internal/verifications/s3-documents-uploaded"


def handler(event, context):
    for record in event.get("Records", []):
        bucket = record["s3"]["bucket"]["name"]
        key = urllib.parse.unquote_plus(record["s3"]["object"]["key"])
        size = record["s3"]["object"].get("size", 0)

        if not key.startswith("verifications/"):
            print(f"[SKIP] key={key} no pertenece al prefijo verifications/")
            continue

        _notify(bucket, key, size)

    return {"statusCode": 200}


def _notify(bucket: str, key: str, size: int) -> None:
    payload = json.dumps({"bucket": bucket, "key": key, "size": size}).encode("utf-8")
    url = VIVIA_API_URL + WEBHOOK_PATH

    req = urllib.request.Request(
        url,
        data=payload,
        headers={
            "Content-Type": "application/json",
            "X-Internal-Api-Key": INTERNAL_API_KEY,
        },
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            print(f"[OK] key={key} → HTTP {response.status}")
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8", errors="replace")
        print(f"[ERROR] key={key} → HTTP {e.code}: {body}")
        raise
    except Exception as e:
        print(f"[ERROR] key={key} → {e}")
        raise
