-- Property media table (images and videos)
CREATE TABLE IF NOT EXISTS property_media (
    id          VARCHAR(50)  NOT NULL,
    property_id VARCHAR(50)  NOT NULL,
    url         VARCHAR(512) NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    classification VARCHAR(50),

    PRIMARY KEY (id),

    CONSTRAINT fk_property_media_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT chk_media_type CHECK (type IN ('IMAGE', 'VIDEO'))
);

CREATE INDEX IF NOT EXISTS idx_property_media_property_id ON property_media(property_id);
CREATE INDEX IF NOT EXISTS idx_property_media_type ON property_media(type);
