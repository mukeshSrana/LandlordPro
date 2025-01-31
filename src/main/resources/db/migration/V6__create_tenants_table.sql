DROP TABLE IF EXISTS tenants;
CREATE TABLE tenants
(
    id                                CHAR(36) PRIMARY KEY,
    full_name                         VARCHAR(255)   NOT NULL,
    date_of_birth                     DATE           NOT NULL,
    phone_number                      VARCHAR(15)    NOT NULL,
    email                             VARCHAR(255)   NOT NULL,
    user_id                           CHAR(36)       NOT NULL,
    apartment_id                      CHAR(36)       NOT NULL,
    lease_start_date                  DATE           NOT NULL,
    lease_end_date                    DATE,
    monthly_rent                      DECIMAL(10, 2) NOT NULL CHECK (monthly_rent >= 0),
    security_deposit                  DECIMAL(10, 2) CHECK (security_deposit >= 0),
    security_deposit_institution_name VARCHAR(255),
    receipt_data                      BLOB,
    private_policy                    BLOB,
    created_date                      TIMESTAMP      NOT NULL,
    updated_date                      TIMESTAMP,
    CONSTRAINT fk_tenant_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_tenant_apartment_id FOREIGN KEY (apartment_id) REFERENCES apartments (id)
);

CREATE INDEX idx_tenant_user_id ON tenants (user_id);
CREATE INDEX idx_tenant_apartment_id ON tenants (apartment_id);


