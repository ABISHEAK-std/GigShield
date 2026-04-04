-- V4__create_grid_and_risk.sql
CREATE TABLE grid_cells (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cell_id VARCHAR(20) UNIQUE NOT NULL,
    center_latitude DECIMAL(10, 7) NOT NULL,
    center_longitude DECIMAL(10, 7) NOT NULL,
    risk_score DECIMAL(5, 2) DEFAULT 0.0,
    active_workers INTEGER DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE risk_factors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    grid_cell VARCHAR(20) NOT NULL,
    factor_type VARCHAR(50) NOT NULL,
    factor_value DECIMAL(5, 2) NOT NULL,
    weight DECIMAL(3, 2) DEFAULT 1.0,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_grid_cells_cell_id ON grid_cells(cell_id);
CREATE INDEX idx_grid_cells_risk_score ON grid_cells(risk_score);
CREATE INDEX idx_risk_factors_grid_cell ON risk_factors(grid_cell);
