DROP INDEX idx_resetpass_session_request_id;
ALTER TABLE resetpass_session DROP COLUMN resetpass_request_id;
ALTER TABLE resetpass_session RENAME TO session;
ALTER INDEX resetpass_session_pkey RENAME TO session_pkey;
