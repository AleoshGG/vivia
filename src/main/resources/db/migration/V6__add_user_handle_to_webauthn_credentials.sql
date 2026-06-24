-- Los registros existentes no tienen userHandle, así que los eliminamos.
-- Los usuarios tendrán que re-registrarse con las nuevas credenciales.
DELETE FROM webauthn_credentials;

-- Ahora agregamos la columna como NOT NULL
ALTER TABLE webauthn_credentials
ADD COLUMN user_handle VARCHAR(512) NOT NULL;
