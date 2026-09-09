-- ============================================================================
-- V12: Create Audit & Transactional Outbox Schema
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS audit;
COMMENT ON SCHEMA audit IS 'Audit module: system audit log entries and transactional outbox events for Kafka dispatch';
