-- ============================================================================
-- V1: Create IAM (Identity & Access Management) Schema
-- ============================================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE SCHEMA IF NOT EXISTS iam;
COMMENT ON SCHEMA iam IS 'IAM module: user credentials, roles, refresh tokens, and authentication audit';
