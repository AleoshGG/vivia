-- Tabla para almacenar credenciales WebAuthn (huella biométrica)
CREATE TABLE webauthn_credentials (
    id VARCHAR(50) PRIMARY KEY,
    credential_id VARCHAR(512) UNIQUE NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    public_key TEXT NOT NULL,
    sign_count BIGINT DEFAULT 0 NOT NULL,
    aaguid VARCHAR(50),
    CONSTRAINT fk_webauthn_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_webauthn_credential_id ON webauthn_credentials(credential_id);
CREATE INDEX idx_webauthn_user_id ON webauthn_credentials(user_id);
