-- Create the `contact` table
CREATE TABLE contact
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY, -- Primary key for the user
    name         VARCHAR(255) NOT NULL,             -- Name of the user, required
    email        VARCHAR(255) NOT NULL,             -- Email of the user, required
    message      TEXT         NOT NULL,             -- Message content, required
    created_date DATE         NOT NULL              -- The date the contactDto was created
);

