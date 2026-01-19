-- Switch callback and confirmation payloads to JSON storage

ALTER TABLE mpesa_payment ADD COLUMN confirmation_payload JSONB;
ALTER TABLE stk_log ADD COLUMN callback_payload JSONB;
ALTER TABLE stk_log ADD COLUMN request_payload JSONB;

CREATE TABLE mpesa_validation_log (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    validation_payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE merchant_config ALTER COLUMN uuid DROP DEFAULT;

ALTER TABLE mpesa_payment ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE mpesa_payment ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE stk_log ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE stk_log ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE mpesa_payment
SET confirmation_payload = jsonb_build_object(
    'TransactionType', transaction_type,
    'BillRefNumber', account_no,
    'MSISDN', phone_number,
    'FirstName', customer_name,
    'BusinessShortCode', paybill_no,
    'TransAmount', transaction_amount,
    'TransID', mpesa_transaction_no,
    'TransTime', to_char(transaction_time, 'YYYYMMDDHH24MISS')
);

UPDATE stk_log
SET callback_payload = jsonb_build_object(
    'Body', jsonb_build_object(
        'stkCallback', jsonb_build_object(
            'MerchantRequestID', merchant_request_id,
            'CheckoutRequestID', checkout_request_id,
            'ResultCode', result_code,
            'ResultDesc', response_description,
            'CallbackMetadata', jsonb_build_object(
                'Item', jsonb_build_array(
                    jsonb_build_object('Name', 'Amount', 'Value', mpesa_payment.transaction_amount),
                    jsonb_build_object('Name', 'MpesaReceiptNumber', 'Value', mpesa_payment.mpesa_transaction_no),
                    jsonb_build_object('Name', 'TransactionDate', 'Value', to_char(mpesa_payment.transaction_time, 'YYYYMMDDHH24MISS')),
                    jsonb_build_object('Name', 'PhoneNumber', 'Value', mpesa_payment.phone_number)
                )
            )
        )
    )
)
FROM mpesa_payment
WHERE stk_log.mpesa_payment_uuid = mpesa_payment.uuid;

UPDATE stk_log
SET request_payload = jsonb_build_object(
    'MerchantRequestID', merchant_request_id,
    'CheckoutRequestID', checkout_request_id,
    'ResponseCode', response_code,
    'ResponseDescription', response_description,
    'CustomerMessage', customer_message
);

ALTER TABLE stk_log DROP COLUMN merchant_request_id;
ALTER TABLE stk_log DROP COLUMN customer_message;
ALTER TABLE stk_log DROP COLUMN checkout_request_id;
ALTER TABLE stk_log DROP COLUMN response_description;
ALTER TABLE stk_log DROP COLUMN response_code;
ALTER TABLE stk_log DROP COLUMN result_code;
ALTER TABLE stk_log DROP COLUMN mpesa_receipt_no;
ALTER TABLE stk_log DROP COLUMN transaction_date;
ALTER TABLE stk_log DROP COLUMN phone_number;
ALTER TABLE stk_log DROP COLUMN transaction_amount;

ALTER TABLE mpesa_payment DROP COLUMN customer_name;
ALTER TABLE mpesa_payment DROP COLUMN phone_number;
ALTER TABLE mpesa_payment DROP COLUMN transaction_amount;
ALTER TABLE mpesa_payment DROP COLUMN transaction_time;
ALTER TABLE mpesa_payment DROP COLUMN paybill_no;
ALTER TABLE mpesa_payment DROP COLUMN mpesa_transaction_no;
ALTER TABLE mpesa_payment DROP COLUMN transaction_type;
ALTER TABLE mpesa_payment DROP COLUMN transaction_status;
ALTER TABLE mpesa_payment DROP COLUMN account_no;

ALTER TABLE stk_log ADD CONSTRAINT uq_stk_log_merchant_request_id UNIQUE (
    (COALESCE(
        callback_payload->'Body'->'stkCallback'->>'MerchantRequestID',
        callback_payload->>'MerchantRequestID'
    ))
);
ALTER TABLE stk_log ADD CONSTRAINT uq_stk_log_checkout_request_id UNIQUE (
    (COALESCE(
        callback_payload->'Body'->'stkCallback'->>'CheckoutRequestID',
        callback_payload->>'CheckoutRequestID'
    ))
);
ALTER TABLE mpesa_payment ADD CONSTRAINT uq_mpesa_payment_trans_id UNIQUE (
    (confirmation_payload->>'TransID')
);

CREATE INDEX idx_mpesa_payment_trans_id_json ON mpesa_payment ((confirmation_payload->>'TransID'));
CREATE INDEX idx_stk_log_merchant_request_id_json ON stk_log ((COALESCE(
    callback_payload->'Body'->'stkCallback'->>'MerchantRequestID',
    callback_payload->>'MerchantRequestID'
)));
CREATE INDEX idx_stk_log_checkout_request_id_json ON stk_log ((COALESCE(
    callback_payload->'Body'->'stkCallback'->>'CheckoutRequestID',
    callback_payload->>'CheckoutRequestID'
)));
CREATE INDEX idx_mpesa_validation_log_uuid ON mpesa_validation_log (uuid);
CREATE INDEX idx_mpesa_validation_log_payload_uuid ON mpesa_validation_log ((validation_payload->>'TransID'));
