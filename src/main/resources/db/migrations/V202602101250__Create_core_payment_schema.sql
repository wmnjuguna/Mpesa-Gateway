-- Baseline schema aligned to current JPA entities and JSON payload model.

CREATE TABLE merchant_config (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    merchant_number INTEGER NOT NULL UNIQUE,
    organisation_name VARCHAR(100) NOT NULL,
    consumer_secret TEXT NOT NULL,
    consumer_key TEXT NOT NULL,
    pass_key TEXT NOT NULL,
    confirmation_url TEXT NOT NULL,
    validation_url TEXT NOT NULL,
    stk_callback_url TEXT NOT NULL,
    response_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_merchant_number_positive CHECK (merchant_number > 0)
);

CREATE TABLE mpesa_payment (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    confirmation_payload JSONB,
    stk_log_uuid VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stk_log (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    callback_payload JSONB,
    request_payload JSONB,
    callback_url TEXT,
    mpesa_payment_uuid VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mpesa_validation_log (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    validation_payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE stk_log
    ADD CONSTRAINT fk_stk_log_mpesa_payment_uuid
        FOREIGN KEY (mpesa_payment_uuid) REFERENCES mpesa_payment (uuid);

CREATE INDEX idx_mpesa_payment_stk_log_uuid ON mpesa_payment (stk_log_uuid);
CREATE INDEX idx_stk_log_mpesa_payment_uuid ON stk_log (mpesa_payment_uuid);
CREATE INDEX idx_mpesa_validation_log_trans_id_json
    ON mpesa_validation_log ((jsonb_extract_path_text(validation_payload, 'TransID')));

CREATE UNIQUE INDEX uq_mpesa_payment_trans_id_json
    ON mpesa_payment ((jsonb_extract_path_text(confirmation_payload, 'TransID')))
    WHERE jsonb_extract_path_text(confirmation_payload, 'TransID') IS NOT NULL
      AND jsonb_extract_path_text(confirmation_payload, 'TransID') <> '';

CREATE UNIQUE INDEX uq_stk_log_merchant_request_id_json
    ON stk_log ((jsonb_extract_path_text(callback_payload, 'Body', 'stkCallback', 'MerchantRequestID')))
    WHERE jsonb_extract_path_text(callback_payload, 'Body', 'stkCallback', 'MerchantRequestID') IS NOT NULL
      AND jsonb_extract_path_text(callback_payload, 'Body', 'stkCallback', 'MerchantRequestID') <> '';

CREATE UNIQUE INDEX uq_stk_log_checkout_request_id_json
    ON stk_log ((jsonb_extract_path_text(callback_payload, 'Body', 'stkCallback', 'CheckoutRequestID')))
    WHERE jsonb_extract_path_text(callback_payload, 'Body', 'stkCallback', 'CheckoutRequestID') IS NOT NULL
      AND jsonb_extract_path_text(callback_payload, 'Body', 'stkCallback', 'CheckoutRequestID') <> '';
