CREATE TABLE IF NOT EXISTS credentials (
    id                     VARCHAR(50)  NOT NULL,
    user_id                VARCHAR(50)  NOT NULL,
    credential_type        VARCHAR(20)  NOT NULL,
    provider_credential_id VARCHAR(255),
    secret_data            TEXT,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT chk_credential_type CHECK (credential_type IN ('PASSWORD', 'GOOGLE', 'BIOMETRIC')),
    CONSTRAINT fk_credentials_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_credentials_user_id ON credentials(user_id);
