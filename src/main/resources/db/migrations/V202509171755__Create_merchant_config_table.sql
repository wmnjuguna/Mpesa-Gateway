uuid -- Create merchant_config table (renamed from paybill_config for universal naming)
CREATE TABLE merchant_config (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    merchant_number INTEGER NOT NULL UNIQUE,
    organisation_name VARCHAR(100) NOT NULL,
    consumer_secret TEXT NOT NULL,
    consumer_key TEXT NOT NULL,
    pass_key TEXT,
    confirmation_url TEXT,
    validation_url TEXT,
    stk_callback_url TEXT,
    response_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add constraint to check merchant_number length (max 10 digits)
ALTER TABLE merchant_config ADD CONSTRAINT chk_merchant_number_length CHECK (merchant_number >= 0 AND merchant_number <= 9999999999);

-- Create index on uuid for performance
CREATE INDEX idx_merchant_config_uuid ON merchant_config(uuid);

-- Create index on merchant_number for performance
CREATE INDEX idx_merchant_config_number ON merchant_config(merchant_number);