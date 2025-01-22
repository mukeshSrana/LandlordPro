CREATE TABLE incomes
(
    id           CHAR(36) PRIMARY KEY,
    apartment_id CHAR(36)       NOT NULL,
    user_id      CHAR(36)       NOT NULL,
    tenant_id    CHAR(36)       NOT NULL,
    date         DATE           NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    status       VARCHAR(20)    NOT NULL,
    comments     VARCHAR(500),
    receipt_data BLOB,
    created_date TIMESTAMP      NOT NULL,
    updated_date TIMESTAMP,
    CONSTRAINT fk_income_apartment_id FOREIGN KEY (apartment_id) REFERENCES apartments (id),
    CONSTRAINT fk_income_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_income_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenants (id)
);

CREATE INDEX idx_income_apartment_id ON incomes (apartment_id);
CREATE INDEX idx_income_user_id ON incomes (user_id);
CREATE INDEX idx_income_tenant_id ON incomes (tenant_id);

