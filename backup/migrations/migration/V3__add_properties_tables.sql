-- Create properties table
CREATE TABLE properties (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    area DOUBLE PRECISION NOT NULL,
    address VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    city VARCHAR(255),
    state VARCHAR(255),
    pincode VARCHAR(20),
    bedrooms INT NOT NULL,
    bathrooms INT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    status VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create property_amenities table for storing amenities
CREATE TABLE property_amenities (
    property_id BIGINT NOT NULL,
    amenity VARCHAR(100) NOT NULL,
    PRIMARY KEY (property_id, amenity),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- Create property_images table for storing image URLs
CREATE TABLE property_images (
    property_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    image_order INT NOT NULL,
    PRIMARY KEY (property_id, image_order),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- Create a trigger to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_properties_updated_at
BEFORE UPDATE ON properties
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column(); 