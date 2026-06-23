-- Address table
CREATE TABLE IF NOT EXISTS address (
    id              VARCHAR(50)   NOT NULL,
    neighborhood_id VARCHAR(50)   NOT NULL,
    street          VARCHAR(100)  NOT NULL,
    exterior_number VARCHAR(10)   NOT NULL,
    interior_number VARCHAR(10),

    PRIMARY KEY (id),

    CONSTRAINT fk_address_neighborhood FOREIGN KEY (neighborhood_id) REFERENCES neighborhood(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_address_neighborhood_id ON address(neighborhood_id);

-- Properties table
CREATE TABLE IF NOT EXISTS properties (
    id                   VARCHAR(50)    NOT NULL,
    lessor_id            VARCHAR(50)    NOT NULL,
    property_type_id     VARCHAR(50)    NOT NULL,
    address_id           VARCHAR(50)    NOT NULL UNIQUE,
    is_available_to_rent BOOLEAN        NOT NULL DEFAULT false,

    title                VARCHAR(200)   NOT NULL,
    description          TEXT           NOT NULL,

    area_m2        DECIMAL(8,2)   NOT NULL,
    bedrooms             SMALLINT       NOT NULL,
    bathrooms            DECIMAL(3,1)   NOT NULL,
    parking_spaces       SMALLINT,
    construction_year    SMALLINT,
    is_condominium       BOOLEAN        NOT NULL DEFAULT false,

    listed_price         DECIMAL(14,2)  NOT NULL,
    price_per_m2         DECIMAL(10,2)  NOT NULL,

    created_at           TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_property_lessor FOREIGN KEY (lessor_id) REFERENCES lessors(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_property_type FOREIGN KEY (property_type_id) REFERENCES property_type(id) ON DELETE RESTRICT,
    CONSTRAINT fk_property_address FOREIGN KEY (address_id) REFERENCES address(id) ON DELETE RESTRICT,
    CONSTRAINT chk_listed_price_positive CHECK (listed_price > 0),
    CONSTRAINT chk_price_per_m2_positive CHECK (price_per_m2 > 0),
    CONSTRAINT chk_bedrooms_valid CHECK (bedrooms >= 0),
    CONSTRAINT chk_bathrooms_valid CHECK (bathrooms > 0)
);

CREATE INDEX IF NOT EXISTS idx_property_lessor_id ON properties(lessor_id);
CREATE INDEX IF NOT EXISTS idx_property_address_id ON properties(address_id);
CREATE INDEX IF NOT EXISTS idx_property_type_id ON properties(property_type_id);
CREATE INDEX IF NOT EXISTS idx_property_is_available_to_rent ON properties(is_available_to_rent);

-- Many-to-many relationship table between properties and amenities
CREATE TABLE IF NOT EXISTS property_amenity (
    property_id VARCHAR(50) NOT NULL,
    amenity_id  VARCHAR(50) NOT NULL,

    PRIMARY KEY (property_id, amenity_id),

    CONSTRAINT fk_property_amenity_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_property_amenity_amenity FOREIGN KEY (amenity_id) REFERENCES amenity(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_property_amenity_property ON property_amenity(property_id);
CREATE INDEX IF NOT EXISTS idx_property_amenity_amenity ON property_amenity(amenity_id);
