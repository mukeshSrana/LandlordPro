-- Create the `contact` table
DROP TABLE IF EXISTS contacts;
CREATE TABLE contacts
(
    id           CHAR(36) PRIMARY KEY, -- Primary key for the user
    reference    VARCHAR(10)  NOT NULL,
    name         VARCHAR(255) NOT NULL,             -- Name of the user, required
    email        VARCHAR(255) NOT NULL,             -- Email of the user, required
    message      TEXT         NOT NULL,             -- Message content, required
    is_deleted   BOOLEAN      DEFAULT FALSE,
    is_resolved  BOOLEAN      DEFAULT FALSE,
    created_date TIMESTAMP    NOT NULL,              -- The date the contactDto was created
    updated_date TIMESTAMP
);

