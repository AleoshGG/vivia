CREATE TABLE lessor_subscriptions (
    user_id       VARCHAR(50)  NOT NULL,
    premium_until TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),

    CONSTRAINT fk_lessor_subscriptions_lessor
        FOREIGN KEY (user_id)
        REFERENCES lessors(user_id)
        ON DELETE CASCADE
);
