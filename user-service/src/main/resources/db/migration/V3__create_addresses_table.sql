CREATE TABLE addresses
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT       NOT NULL UNIQUE REFERENCES users (id),
    recipient_name VARCHAR(255) NOT NULL,
    line1          VARCHAR(255) NOT NULL,
    line2          VARCHAR(255),
    city           VARCHAR(100) NOT NULL,
    state          VARCHAR(100) NOT NULL,
    postal_code    VARCHAR(20)  NOT NULL,
    country        VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);
CREATE INDEX idx_addresses_user_id ON addresses (user_id);