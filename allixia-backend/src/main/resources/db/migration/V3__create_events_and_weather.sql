-- V3__create_events_and_weather.sql
CREATE TABLE disaster_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    eonet_id VARCHAR(50) UNIQUE,
    title VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    grid_cell VARCHAR(20),
    severity VARCHAR(20),
    status VARCHAR(20),
    event_date TIMESTAMP,
    source VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE weather_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    grid_cell VARCHAR(20) NOT NULL,
    temperature DECIMAL(5, 2),
    rainfall DECIMAL(6, 2),
    humidity DECIMAL(5, 2),
    wind_speed DECIMAL(5, 2),
    conditions VARCHAR(100),
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_disaster_events_grid_cell ON disaster_events(grid_cell);
CREATE INDEX idx_disaster_events_event_type ON disaster_events(event_type);
CREATE INDEX idx_disaster_events_event_date ON disaster_events(event_date);
CREATE INDEX idx_weather_data_grid_cell ON weather_data(grid_cell);
CREATE INDEX idx_weather_data_timestamp ON weather_data(timestamp);
