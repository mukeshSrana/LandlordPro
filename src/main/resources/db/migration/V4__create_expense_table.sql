DROP TABLE IF EXISTS expenses;
CREATE TABLE expenses
(
    id               CHAR(36) PRIMARY KEY,
    apartment_id     CHAR(36)       NOT NULL,
    user_id          CHAR(36)       NOT NULL,
    category         VARCHAR(255)   NOT NULL,
    name             VARCHAR(255)   NOT NULL,
    amount           DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    expense_location VARCHAR(255)   NOT NULL,
    date             DATE           NOT NULL,
    receipt_data     BLOB,
    created_date     TIMESTAMP      NOT NULL,
    updated_date     TIMESTAMP,
    CONSTRAINT fk_expense_apartment_id FOREIGN KEY (apartment_id) REFERENCES apartments (id),
    CONSTRAINT fk_expense_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_expense_apartment_id ON expenses (apartment_id);
CREATE INDEX idx_expense_user_id ON expenses (user_id);
