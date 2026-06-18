CREATE TABLE IF NOT EXISTS users (
    id               VARCHAR(50)  NOT NULL,
    name             VARCHAR(100) NOT NULL,
    paternal_surname VARCHAR(100) NOT NULL,
    maternal_surname VARCHAR(100) NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    photo_url        VARCHAR(512) NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
