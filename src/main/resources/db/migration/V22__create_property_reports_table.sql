CREATE TABLE property_reports (
    id             VARCHAR(50)  NOT NULL,
    property_id    VARCHAR(50),
    property_title VARCHAR(200) NOT NULL,
    lessor_id      VARCHAR(50)  NOT NULL,
    lessee_id      VARCHAR(50)  NOT NULL,
    reason_id      VARCHAR(50)  NOT NULL,
    comment        TEXT,
    is_resolved    BOOLEAN      NOT NULL DEFAULT FALSE,
    verdict        VARCHAR(50),
    resolved_by    VARCHAR(50),
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    resolved_at    TIMESTAMPTZ,

    CONSTRAINT pk_property_reports   PRIMARY KEY (id),
    CONSTRAINT fk_report_property    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL,
    CONSTRAINT fk_report_lessor      FOREIGN KEY (lessor_id)   REFERENCES lessors(user_id),
    CONSTRAINT fk_report_lessee      FOREIGN KEY (lessee_id)   REFERENCES lessees(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_report_reason      FOREIGN KEY (reason_id)   REFERENCES report_reasons(id),
    CONSTRAINT fk_report_admin       FOREIGN KEY (resolved_by) REFERENCES users(id),
    CONSTRAINT uq_report_per_lessee  UNIQUE (property_id, lessee_id)
);
