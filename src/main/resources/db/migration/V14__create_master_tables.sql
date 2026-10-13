-- ============================================================================
-- V14: Master Data Module Core Tables (MySQL 8.0)
-- ============================================================================

-- Ensure master schema exists
CREATE SCHEMA IF NOT EXISTS master;

-- 1. Cities table
CREATE TABLE IF NOT EXISTS master.cities (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'India',
    pincode VARCHAR(20) NULL,
    is_popular BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    latitude DECIMAL(10, 8) NULL,
    longitude DECIMAL(11, 8) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_master_city_name_state UNIQUE (name, state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_master_cities_popular ON master.cities(is_popular, status);
CREATE INDEX idx_master_cities_name ON master.cities(name);

-- 2. Languages table
CREATE TABLE IF NOT EXISTS master.languages (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Genres table
CREATE TABLE IF NOT EXISTS master.genres (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Certifications table
CREATE TABLE IF NOT EXISTS master.certifications (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    minimum_age INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed Popular Indian Cities
INSERT IGNORE INTO master.cities (id, name, state, country, is_popular, status) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'Mumbai', 'Maharashtra', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000002', 'Delhi-NCR', 'Delhi', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000003', 'Bengaluru', 'Karnataka', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000004', 'Hyderabad', 'Telangana', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000005', 'Chennai', 'Tamil Nadu', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000006', 'Kolkata', 'West Bengal', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000007', 'Pune', 'Maharashtra', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000008', 'Ahmedabad', 'Gujarat', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000009', 'Jaipur', 'Rajasthan', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000010', 'Indore', 'Madhya Pradesh', 'India', TRUE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000011', 'Chandigarh', 'Punjab', 'India', FALSE, 'ACTIVE'),
    ('b0000000-0000-0000-0000-000000000012', 'Kochi', 'Kerala', 'India', FALSE, 'ACTIVE');

-- Seed Languages
INSERT IGNORE INTO master.languages (id, name, code) VALUES
    ('b1000000-0000-0000-0000-000000000001', 'Hindi', 'hi'),
    ('b1000000-0000-0000-0000-000000000002', 'English', 'en'),
    ('b1000000-0000-0000-0000-000000000003', 'Telugu', 'te'),
    ('b1000000-0000-0000-0000-000000000004', 'Tamil', 'ta'),
    ('b1000000-0000-0000-0000-000000000005', 'Malayalam', 'ml'),
    ('b1000000-0000-0000-0000-000000000006', 'Kannada', 'kn'),
    ('b1000000-0000-0000-0000-000000000007', 'Marathi', 'mr'),
    ('b1000000-0000-0000-0000-000000000008', 'Bengali', 'bn'),
    ('b1000000-0000-0000-0000-000000000009', 'Gujarati', 'gu'),
    ('b1000000-0000-0000-0000-000000000010', 'Punjabi', 'pa');

-- Seed Genres
INSERT IGNORE INTO master.genres (id, name, code, description) VALUES
    ('b2000000-0000-0000-0000-000000000001', 'Action', 'ACTION', 'High-energy, stunts, and physical fights'),
    ('b2000000-0000-0000-0000-000000000002', 'Drama', 'DRAMA', 'Narratives with high emotional development'),
    ('b2000000-0000-0000-0000-000000000003', 'Comedy', 'COMEDY', 'Humorous films designed to provoke laughter'),
    ('b2000000-0000-0000-0000-000000000004', 'Thriller', 'THRILLER', 'Suspenseful plots full of excitement'),
    ('b2000000-0000-0000-0000-000000000005', 'Romance', 'ROMANCE', 'Stories of intimate passion and love'),
    ('b2000000-0000-0000-0000-000000000006', 'Sci-Fi', 'SCIFI', 'Speculative fiction involving futuristic technology'),
    ('b2000000-0000-0000-0000-000000000007', 'Horror', 'HORROR', 'Films designed to cause fear and panic'),
    ('b2000000-0000-0000-0000-000000000008', 'Animation', 'ANIMATION', 'Illustrated and computer-animated films'),
    ('b2000000-0000-0000-0000-000000000009', 'Adventure', 'ADVENTURE', 'Exciting stories involving journeys and quests'),
    ('b2000000-0000-0000-0000-000000000010', 'Crime', 'CRIME', 'Underworld, law enforcement, and mystery');

-- Seed Certifications
INSERT IGNORE INTO master.certifications (id, code, name, description, minimum_age) VALUES
    ('b3000000-0000-0000-0000-000000000001', 'U', 'Universal', 'Unrestricted public exhibition suitable for all ages', 0),
    ('b3000000-0000-0000-0000-000000000002', 'UA', 'Parental Guidance', 'Suitable for all, but parental discretion advised under 12', 12),
    ('b3000000-0000-0000-0000-000000000003', 'A', 'Adults Only', 'Restricted to viewers aged 18 and older', 18),
    ('b3000000-0000-0000-0000-000000000004', 'S', 'Specialized', 'Restricted to specialized audiences like doctors/scientists', 18);
