CREATE TABLE IF NOT EXISTS verification_rejections (
    id          VARCHAR(50)  NOT NULL,
    lessor_id   VARCHAR(50)  NOT NULL,
    comment     TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_verification_rejections PRIMARY KEY (id),
    CONSTRAINT uq_verification_rejections_lessor UNIQUE (lessor_id),
    CONSTRAINT fk_verification_rejections_lessor
        FOREIGN KEY (lessor_id) REFERENCES lessors(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS verification_rejection_reasons (
    rejection_id  VARCHAR(50)   NOT NULL,
    reason        VARCHAR(255)  NOT NULL,

    CONSTRAINT fk_rejection_reasons_rejection
        FOREIGN KEY (rejection_id) REFERENCES verification_rejections(id) ON DELETE CASCADE
);
