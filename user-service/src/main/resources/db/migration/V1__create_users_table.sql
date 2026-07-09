CREATE TABLE users
(
    id                    BIGSERIAL PRIMARY KEY,
    email                 VARCHAR(255) NOT NULL UNIQUE,
    password              VARCHAR(255) NOT NULL,
    first_name            VARCHAR(100) NOT NULL,
    last_name             VARCHAR(100),
    is_active             BOOLEAN      NOT NULL DEFAULT TRUE,
    is_email_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    terms_accepted_at     TIMESTAMP    NOT NULL,
    last_login_at         TIMESTAMP,
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    account_locked_at     TIMESTAMP,
    password_changed_at   TIMESTAMP    NOT NULL,
    role                  VARCHAR(20)  NOT NULL,
    created_at            TIMESTAMP    NOT NULL,
    updated_at            TIMESTAMP    NOT NULL
);

CREATE INDEX idx_users_email ON users (email);