-- Create all required tables for Car Listings Service
-- Run this in H2 Console at http://localhost:8081/h2-console

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create cars table (renamed 'year' to 'year_made')
CREATE TABLE IF NOT EXISTS cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year_made INTEGER NOT NULL,
    mileage INTEGER NOT NULL,
    condition VARCHAR(50) NOT NULL,
    asking_price DECIMAL(19,2) NOT NULL,
    description CLOB,
    color VARCHAR(100),
    fuel_type VARCHAR(100),
    transmission VARCHAR(100),
    number_of_doors INTEGER,
    engine_size VARCHAR(50),
    location VARCHAR(255),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    seller_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES users(id)
);

-- Create saved_searches table
CREATE TABLE IF NOT EXISTS saved_searches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    buyer_id BIGINT NOT NULL,
    make VARCHAR(255),
    model VARCHAR(255),
    min_year INTEGER,
    max_year INTEGER,
    max_mileage INTEGER,
    min_price DECIMAL(19,2),
    max_price DECIMAL(19,2),
    condition VARCHAR(50),
    fuel_type VARCHAR(100),
    transmission VARCHAR(100),
    location VARCHAR(255),
    email_notifications BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (buyer_id) REFERENCES users(id)
);

-- Create favourites table
CREATE TABLE IF NOT EXISTS favourites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (car_id) REFERENCES cars(id),
    UNIQUE(buyer_id, car_id)
);

-- Insert test users
INSERT INTO users (username, first_name, last_name, email, phone_number, password, role, active, created_at, updated_at) VALUES
('johndoe', 'John', 'Doe', 'john.doe@example.com', '+61400123456', 'password123', 'SELLER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('janesmith', 'Jane', 'Smith', 'jane.smith@example.com', '+61400987654', 'password123', 'BOTH', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bobbuyer', 'Bob', 'Johnson', 'bob.johnson@example.com', '+61400555777', 'password123', 'BUYER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Verify tables and users were created
SELECT 'Users created:' AS info;
SELECT id, username, first_name, last_name, email, role FROM users;

SELECT 'Tables created:' AS info;
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC';
