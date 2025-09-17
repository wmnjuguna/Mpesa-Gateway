-- Create stk_log table
CREATE TABLE stk_log (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    merchant_request_id VARCHAR(255),
    customer_message TEXT,
    checkout_request_id VARCHAR(255),
    response_description TEXT,
    response_code INTEGER,
    result_code INTEGER,
    mpesa_receipt_no VARCHAR(255),
    callback_url TEXT,
    mpesa_payment_uuid VARCHAR(36),
    FOREIGN KEY (mpesa_payment_uuid) REFERENCES mpesa_payment(uuid)
);

-- Create index on uuid for performance
CREATE INDEX idx_stk_log_uuid ON stk_log(uuid);

-- Create index on checkout_request_id for performance
CREATE INDEX idx_stk_log_checkout_request ON stk_log(checkout_request_id);

-- Create index on merchant_request_id for performance
CREATE INDEX idx_stk_log_merchant_request ON stk_log(merchant_request_id);

-- Create index on mpesa_payment_uuid for foreign key performance
CREATE INDEX idx_stk_log_payment_uuid ON stk_log(mpesa_payment_uuid);