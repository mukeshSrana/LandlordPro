-- Create the `contact` table
DROP TABLE IF EXISTS contacts;
CREATE TABLE contacts
(
    id           CHAR(36) PRIMARY KEY, -- Primary key for the user
    name         VARCHAR(255) NOT NULL,             -- Name of the user, required
    email        VARCHAR(255) NOT NULL,             -- Email of the user, required
    message      TEXT         NOT NULL,             -- Message content, required
    created_date DATE         NOT NULL,              -- The date the contactDto was created
    user_id     CHAR(36)      NOT NULL,
    CONSTRAINT fk_contact_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

