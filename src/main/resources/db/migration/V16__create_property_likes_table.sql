CREATE TABLE property_likes (
    user_id     VARCHAR(50) NOT NULL,
    property_id VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, property_id),

    CONSTRAINT fk_likes_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_likes_property
        FOREIGN KEY (property_id)
        REFERENCES properties(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_likes_user_id     ON property_likes(user_id);
CREATE INDEX idx_likes_property_id ON property_likes(property_id);
