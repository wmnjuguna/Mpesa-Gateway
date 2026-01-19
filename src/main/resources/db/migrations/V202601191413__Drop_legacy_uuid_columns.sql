-- Backfill BaseEntity uuid from legacy columns
UPDATE merchant_config SET uuid = COALESCE(uuid, paybill_uid) WHERE paybill_uid IS NOT NULL;
UPDATE mpesa_payment SET uuid = COALESCE(uuid, mpesa_payment_uid) WHERE mpesa_payment_uid IS NOT NULL;
UPDATE stk_log SET uuid = COALESCE(uuid, stk_log_uid) WHERE stk_log_uid IS NOT NULL;
UPDATE mpesa_validation_log SET uuid = COALESCE(uuid, validation_log_uid) WHERE validation_log_uid IS NOT NULL;

-- Enforce uuid presence after backfill
ALTER TABLE merchant_config ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE mpesa_payment ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE stk_log ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE mpesa_validation_log ALTER COLUMN uuid SET NOT NULL;

-- Ensure uuid uniqueness after backfill
ALTER TABLE merchant_config ADD CONSTRAINT merchant_config_uuid_key UNIQUE (uuid);
ALTER TABLE mpesa_payment ADD CONSTRAINT mpesa_payment_uuid_key UNIQUE (uuid);
ALTER TABLE stk_log ADD CONSTRAINT stk_log_uuid_key UNIQUE (uuid);
ALTER TABLE mpesa_validation_log ADD CONSTRAINT mpesa_validation_log_uuid_key UNIQUE (uuid);

-- Drop legacy *_uid columns now managed by BaseEntity
ALTER TABLE merchant_config DROP COLUMN IF EXISTS paybill_uid;
ALTER TABLE mpesa_payment DROP COLUMN IF EXISTS mpesa_payment_uid;
ALTER TABLE stk_log DROP COLUMN IF EXISTS stk_log_uid;
ALTER TABLE mpesa_validation_log DROP COLUMN IF EXISTS validation_log_uid;
