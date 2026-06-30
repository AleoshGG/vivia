ALTER TABLE lessors
    ADD COLUMN IF NOT EXISTS verification_status VARCHAR(20) NOT NULL DEFAULT 'UNVERIFIED';

CREATE TABLE IF NOT EXISTS lessor_documents (
    id            VARCHAR(50)  NOT NULL,
    lessor_id     VARCHAR(50)  NOT NULL,
    document_type VARCHAR(10)  NOT NULL,
    uri           VARCHAR(512) NOT NULL,
    uploaded_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_documents_lessor
        FOREIGN KEY (lessor_id) REFERENCES lessors(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_lessor_documents_lessor_id ON lessor_documents(lessor_id);
