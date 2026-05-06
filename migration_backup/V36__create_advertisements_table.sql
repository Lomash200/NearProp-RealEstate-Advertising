CREATE TABLE advertisements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    banner_image_url VARCHAR(255) NOT NULL,
    website_url VARCHAR(255),
    whatsapp_number VARCHAR(20),
    target_location VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    radius_km DOUBLE PRECISION NOT NULL,
    district_id BIGINT,
    district_name VARCHAR(255),
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    
    CONSTRAINT fk_advertisement_user FOREIGN KEY (created_by) REFERENCES users(id)
); 