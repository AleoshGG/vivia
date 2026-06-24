-- Migración: Eliminar tabla refresh_tokens
-- Los refresh tokens ahora se almacenan en Redis con TTL automático

DROP TABLE IF EXISTS refresh_tokens;
