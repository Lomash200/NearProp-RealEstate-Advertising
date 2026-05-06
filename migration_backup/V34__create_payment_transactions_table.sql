-- Create payment_transactions table
CREATE TABLE payment_transactions (
    id SERIAL PRIMARY KEY,
    reference_id VARCHAR(50) NOT NULL UNIQUE,
    gateway_transaction_id VARCHAR(100),
    gateway_order_id VARCHAR(100),
    user_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    payment_type VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    failure_message TEXT,
    failure_code VARCHAR(50),
    subscription_id BIGINT,
    receipt_url VARCHAR(255),
    refund_reference_id VARCHAR(100),
    refund_amount NUMERIC(10, 2),
    refund_reason TEXT,
    refund_status VARCHAR(20),
    gateway_response TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    payment_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL
);

-- Create index for faster lookups
CREATE INDEX idx_payment_transactions_user_id ON payment_transactions(user_id);
CREATE INDEX idx_payment_transactions_status ON payment_transactions(status);
CREATE INDEX idx_payment_transactions_subscription_id ON payment_transactions(subscription_id);
CREATE INDEX idx_payment_transactions_created_at ON payment_transactions(created_at);

-- Add receipt_url and payment_transaction_reference to subscriptions table
ALTER TABLE subscriptions ADD COLUMN receipt_url VARCHAR(255);
ALTER TABLE subscriptions ADD COLUMN payment_transaction_reference VARCHAR(50);

-- Add foreign key reference to connect subscriptions with payment transactions
ALTER TABLE subscriptions 
    ADD CONSTRAINT fk_subscription_payment_transaction 
    FOREIGN KEY (payment_transaction_reference) 
    REFERENCES payment_transactions(reference_id) 
    ON DELETE SET NULL; 