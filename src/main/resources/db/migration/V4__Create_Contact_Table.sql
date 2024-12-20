-- Create the `contact` table
CREATE TABLE contact
(
    id           VARCHAR(36)  NOT NULL PRIMARY KEY, -- UUID with a length of 36 characters
    name         VARCHAR(255) NOT NULL,             -- Name of the user, required
    email        VARCHAR(255) NOT NULL,             -- Email of the user, required
    message      TEXT         NOT NULL,             -- Message content, required
    created_date DATE         NOT NULL              -- The date the contactDto was created
);

