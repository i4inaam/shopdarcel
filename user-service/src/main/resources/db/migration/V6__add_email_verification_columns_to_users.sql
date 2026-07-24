ALTER TABLE users
    ADD COLUMN email_token_hash VARCHAR(255);
ALTER TABLE users
    ADD COLUMN email_token_expires_at TIMESTAMP;
CREATE INDEX idx_users_email_token_hash ON users (email_token_hash);