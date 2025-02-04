-- Migration V2: Drop and recreate the users table with mobile_nr

-- Step 1: Drop the existing users table (if it exists)
DROP TABLE IF EXISTS users;

-- Step 2: Create the new users table with the desired schema
CREATE TABLE users
(
    id                      CHAR(36) PRIMARY KEY,                  -- Primary key for the user
    name                    VARCHAR(255) NOT NULL,                 -- User's name
    username                VARCHAR(255) NOT NULL UNIQUE,          -- Username (replaces email)
    password                VARCHAR(255) NOT NULL,                 -- Encrypted password
    user_role               VARCHAR(50)  NOT NULL,
    enabled                 BOOLEAN      NOT NULL DEFAULT TRUE,    -- Whether the account is enabled
    account_non_expired     BOOLEAN      NOT NULL DEFAULT TRUE,    -- Account expiry status
    credentials_non_expired BOOLEAN      NOT NULL DEFAULT TRUE,    -- Credentials expiry status
    account_non_locked      BOOLEAN      NOT NULL DEFAULT TRUE,    -- Account locked status
    mobile_nr               VARCHAR(15)  NOT NULL,                -- Mobile number
    is_deleted              BOOLEAN      DEFAULT FALSE,            -- Whether the account is deleted (soft delete)
    accept_consent          BOOLEAN      DEFAULT FALSE,            -- Whether the user accepted consent
    accept_tenant_data_responsibility BOOLEAN DEFAULT FALSE,           -- Whether the user accepted tenant data responsibility
    created_date            TIMESTAMP    NOT NULL,
    updated_date            TIMESTAMP
);
ALTER TABLE users ADD CONSTRAINT uq_users_username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT chk_users_role CHECK (user_role IN ('ROLE_ADMIN', 'ROLE_LANDLORD', 'ROLE_MANAGER'));

CREATE UNIQUE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_user_role ON users (user_role);
