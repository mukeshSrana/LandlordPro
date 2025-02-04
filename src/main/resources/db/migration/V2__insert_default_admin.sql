-- Ensure ROLE_ADMIN user is inserted only if it doesn't exist
INSERT INTO users (id, name, username, password, user_role, enabled, account_non_expired,
                   credentials_non_expired, account_non_locked, mobile_nr, is_deleted,
                   accept_consent, accept_tenant_data_responsibility, created_date,updated_date)
SELECT
    RANDOM_UUID(),
    'LandLordAdmin',
    'landlordpronorway@gmail.com',
    '${adminPassword}',
    'ROLE_ADMIN',
    TRUE, TRUE, TRUE, TRUE,
    '99107167',
    FALSE, TRUE, TRUE,
    NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'landlordpronorway@gmail.com');
