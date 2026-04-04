-- V6__create_claims_and_payouts.sql
CREATE TABLE claim_triggers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id UUID REFERENCES disaster_events(id),
    trigger_type VARCHAR(50) NOT NULL,
    grid_cell VARCHAR(20) NOT NULL,
    threshold_value DECIMAL(10, 2),
    actual_value DECIMAL(10, 2),
    triggered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE claims (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_number VARCHAR(20) UNIQUE NOT NULL,
    policy_id UUID NOT NULL REFERENCES insurance_policies(id),
    user_id UUID NOT NULL REFERENCES users(id),
    trigger_id UUID REFERENCES claim_triggers(id),
    claim_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    auto_approved BOOLEAN DEFAULT FALSE,
    fraud_score DECIMAL(5, 2),
    filed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fraud_checks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL REFERENCES claims(id),
    check_type VARCHAR(50) NOT NULL,
    passed BOOLEAN NOT NULL,
    details TEXT,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payouts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL REFERENCES claims(id),
    user_id UUID NOT NULL REFERENCES users(id),
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    wallet_address VARCHAR(42),
    transaction_hash VARCHAR(66),
    blockchain_confirmed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_claims_policy_id ON claims(policy_id);
CREATE INDEX idx_claims_user_id ON claims(user_id);
CREATE INDEX idx_claims_status ON claims(status);
CREATE INDEX idx_fraud_checks_claim_id ON fraud_checks(claim_id);
CREATE INDEX idx_payouts_claim_id ON payouts(claim_id);
CREATE INDEX idx_payouts_user_id ON payouts(user_id);
