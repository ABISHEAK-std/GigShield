-- V5__create_policies.sql
CREATE TABLE insurance_policies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    policy_number VARCHAR(20) UNIQUE NOT NULL,
    coverage_type VARCHAR(50) NOT NULL,
    coverage_amount DECIMAL(10, 2) NOT NULL,
    premium_amount DECIMAL(10, 2) NOT NULL,
    risk_score DECIMAL(5, 2),
    status VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    blockchain_hash VARCHAR(66),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_policies_user_id ON insurance_policies(user_id);
CREATE INDEX idx_policies_policy_number ON insurance_policies(policy_number);
CREATE INDEX idx_policies_status ON insurance_policies(status);
