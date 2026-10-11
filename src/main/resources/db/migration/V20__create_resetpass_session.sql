CREATE TABLE resetpass_session (
    session_id          UUID PRIMARY KEY,
    email               VARCHAR(255) NOT NULL,
    expire_at           TIMESTAMP NOT NULL,
    resetpass_request_id BIGINT NOT NULL REFERENCES resetpass_request (id),
    created_at          TIMESTAMP DEFAULT now(),
    updated_at          TIMESTAMP DEFAULT now()
);
CREATE INDEX idx_resetpass_session_request_id ON resetpass_session (resetpass_request_id);
