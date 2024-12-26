CREATE TABLE apartments
(
    id                   CHAR(36) PRIMARY KEY,                        -- UUID primary key for the apartment
    apartment_short_name VARCHAR(255) NOT NULL,                       -- Apartment short name
    owner_name           VARCHAR(100) NOT NULL,
    address_line1        VARCHAR(255) NOT NULL,                       -- First address line
    address_line2        VARCHAR(255),                                -- Second address line
    pincode              VARCHAR(10)  NOT NULL,                       -- Pincode (non-nullable)
    city                 VARCHAR(100) NOT NULL,                       -- City
    country              VARCHAR(100) NOT NULL,                       -- Country
    created_date         DATE         NOT NULL,                       -- Date created
    updated_date         DATE,                                        -- Last update date
    user_id              CHAR(36)     NOT NULL,                       -- Foreign key to users table
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id), -- Foreign key constraint
    CONSTRAINT unique_apartment_user UNIQUE (apartment_short_name, user_id) -- Composite unique constraint
);

-- Optionally, index on user_id for performance
CREATE INDEX idx_user_id ON apartments (user_id);
