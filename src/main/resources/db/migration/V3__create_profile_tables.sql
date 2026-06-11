CREATE TABLE IF NOT EXISTS lessors (
    user_id      VARCHAR(50) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lessees (
    user_id    VARCHAR(50) NOT NULL,
    latitude   DECIMAL(12, 9),
    longitude  DECIMAL(12, 9),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_lessors_user_id ON lessors(user_id);
CREATE INDEX IF NOT EXISTS idx_lessees_user_id ON lessees(user_id);
