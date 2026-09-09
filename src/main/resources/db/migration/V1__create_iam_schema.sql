-- ============================================================================
-- V1: Create IAM (Identity & Access Management) Schema
-- ============================================================================
-- MySQL 8.0
--
-- IAM module:
--   - User credentials
--   - Roles
--   - Refresh tokens
--   - Authentication audit
--
-- UUID generation is handled by the application layer.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS iam;