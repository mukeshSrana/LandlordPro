-- Migration V3: Create user_roles table to link users and roles
DROP TABLE IF EXISTS user_roles;
-- Step 1: Create the user_roles table
CREATE TABLE user_roles
(
    user_id CHAR(36),
    role    VARCHAR(50),
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);
