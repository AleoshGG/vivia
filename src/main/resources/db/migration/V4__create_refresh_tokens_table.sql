CREATE TABLE IF NOT EXISTS refresh_tokens (
    id               VARCHAR(50)  NOT NULL,
    user_identifier  VARCHAR(255) NOT NULL,
    token            VARCHAR(512) NOT NULL UNIQUE,
    role             VARCHAR(50)  NOT NULL,
    expiry_date      TIMESTAMPTZ  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens(user_identifier);
