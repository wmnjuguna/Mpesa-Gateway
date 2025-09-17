-- Create mpesa_payment table
CREATE TABLE mpesa_payment (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    customer_name VARCHAR(50),
    phone_number VARCHAR(15) NOT NULL,
    transaction_amount DECIMAL(10,2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    paybill_no VARCHAR(10),
    mpesa_transaction_no VARCHAR(10),
    transaction_type VARCHAR(20),
    transaction_status BOOLEAN,
    account_no VARCHAR(20),
    transaction_operation VARCHAR(2),
    stk_log_uuid VARCHAR(36)
);

-- Create index on uuid for performance
CREATE INDEX idx_mpesa_payment_uuid ON mpesa_payment(uuid);

-- Create index on phone_number for performance
CREATE INDEX idx_mpesa_payment_phone ON mpesa_payment(phone_number);

-- Create index on mpesa_transaction_no for performance
CREATE INDEX idx_mpesa_payment_transaction_no ON mpesa_payment(mpesa_transaction_no);

-- Create index on transaction_time for performance
CREATE INDEX idx_mpesa_payment_transaction_time ON mpesa_payment(transaction_time);