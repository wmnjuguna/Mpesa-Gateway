CREATE DATABASE IF NOT EXISTS payments;

CREATE TABLE mpesa_payments (
sn SERIAL PRIMARY KEY,
customer_name VARCHAR(200),
phone_number VARCHAR(20),
transaction_amount NUMERIC(10,2),
transactionTime TIMESTAMP,
paybill_no VARCHAR(10),
receipt_no VARCHAR(10),
transaction_type VARCHAR(20),
account_no VARCHAR(20) NOT NULL
);

CREATE TABLE stk_log (
    id SERIAL PRIMARY KEY,
    uuid UUID,
    merchant_request_id VARCHAR(50) NOT NULL,
    customer_message VARCHAR(50),
    checkout_request_id VARCHAR(50) NOT NULL,
    response_description VARCHAR(50),
    response_code INTEGER,
    result_code INTEGER,
    mpesa_receipt_no VARCHAR(15)
);
