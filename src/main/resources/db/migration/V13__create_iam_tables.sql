-- ============================================================================
-- V13: IAM Module Core Tables (MySQL 8.0)
-- ============================================================================

-- Ensure iam schema exists
CREATE SCHEMA IF NOT EXISTS iam;

-- 1. Roles table
CREATE TABLE IF NOT EXISTS iam.roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed system roles (using INSERT IGNORE for idempotency)
INSERT IGNORE INTO iam.roles (id, name, description) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'ROLE_CUSTOMER', 'Standard customer booking movie tickets'),
    ('a0000000-0000-0000-0000-000000000002', 'ROLE_THEATRE_OWNER', 'Theatre and screen operations manager'),
    ('a0000000-0000-0000-0000-000000000003', 'ROLE_ADMIN', 'Platform super administrator'),
    ('a0000000-0000-0000-0000-000000000004', 'ROLE_SUPPORT', 'Customer support personnel');

-- 2. Users table
CREATE TABLE IF NOT EXISTS iam.users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_iam_users_email UNIQUE (email),
    CONSTRAINT uq_iam_users_phone UNIQUE (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_iam_users_email ON iam.users(email);
CREATE INDEX idx_iam_users_phone ON iam.users(phone_number);

-- 3. User Roles mapping table
CREATE TABLE IF NOT EXISTS iam.user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES iam.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES iam.roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Refresh Tokens table
CREATE TABLE IF NOT EXISTS iam.refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME(6) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    replaced_by_token_hash VARCHAR(255) NULL,
    device_info VARCHAR(255),
    ip_address VARCHAR(50),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES iam.users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_iam_refresh_tokens_user ON iam.refresh_tokens(user_id);
CREATE INDEX idx_iam_refresh_tokens_hash ON iam.refresh_tokens(token_hash);

-- 5. OTP Verifications table (for Password Reset, Email, Phone verification)
CREATE TABLE IF NOT EXISTS iam.otp_verifications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_otp_verifications_user FOREIGN KEY (user_id) REFERENCES iam.users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_iam_otp_user_purpose ON iam.otp_verifications(user_id, purpose);
