ALTER TABLE resetpass_request ADD COLUMN session_id UUID REFERENCES session (session_id);
CREATE INDEX idx_resetpass_request_session_id ON resetpass_request (session_id);
