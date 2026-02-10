ALTER TABLE merchant_config
    ADD COLUMN registration_status VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
    ADD COLUMN registration_failure_reason TEXT;

CREATE INDEX idx_merchant_config_registration_status
    ON merchant_config (registration_status);
