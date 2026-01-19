-- Drop legacy *_uid indexes/constraints after BaseEntity switch

DROP INDEX IF EXISTS idx_merchant_config_paybill_uid;
DROP INDEX IF EXISTS idx_mpesa_payment_mpesa_payment_uid;
DROP INDEX IF EXISTS idx_stk_log_stk_log_uid;
DROP INDEX IF EXISTS idx_mpesa_validation_log_validation_log_uid;

ALTER TABLE merchant_config DROP CONSTRAINT IF EXISTS merchant_config_paybill_uid_key;
ALTER TABLE mpesa_payment DROP CONSTRAINT IF EXISTS mpesa_payment_mpesa_payment_uid_key;
ALTER TABLE stk_log DROP CONSTRAINT IF EXISTS stk_log_stk_log_uid_key;
ALTER TABLE mpesa_validation_log DROP CONSTRAINT IF EXISTS mpesa_validation_log_validation_log_uid_key;
