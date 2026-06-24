-- Property types catalog
CREATE TABLE IF NOT EXISTS property_type (
    id   VARCHAR(50)  NOT NULL,
    name VARCHAR(50)  NOT NULL,

    PRIMARY KEY (id)
);

-- Amenities catalog
CREATE TABLE IF NOT EXISTS amenity (
    id   VARCHAR(50)  NOT NULL,
    name VARCHAR(80)  NOT NULL,

    PRIMARY KEY (id)
);

-- Neighborhoods catalog
-- City and state are fixed: San Cristóbal de las Casas, Chiapas
CREATE TABLE IF NOT EXISTS neighborhood (
    id          VARCHAR(50)   NOT NULL,
    name        VARCHAR(100)  NOT NULL,
    postal_code CHAR(5),

    PRIMARY KEY (id)
);

