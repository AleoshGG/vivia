CREATE TABLE IF NOT EXISTS admins (
    user_id VARCHAR(50) NOT NULL,
    CONSTRAINT pk_admins PRIMARY KEY (user_id),
    CONSTRAINT fk_admins_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
