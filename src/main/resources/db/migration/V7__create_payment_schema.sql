-- ============================================================================
-- V7: Create Payment Schema
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS payment;
COMMENT ON SCHEMA payment IS 'Payment module: payment transactions, idempotency records, and refund ledgers';
