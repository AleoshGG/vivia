-- Fix integer column types from SMALLINT to INTEGER in properties table
-- This resolves schema validation errors where Hibernate expects INTEGER but finds SMALLINT

ALTER TABLE properties
    ALTER COLUMN bedrooms TYPE INTEGER,
    ALTER COLUMN parking_spaces TYPE INTEGER,
    ALTER COLUMN construction_year TYPE INTEGER;
