ALTER TABLE address ADD COLUMN location geography(Point, 4326);
CREATE INDEX IF NOT EXISTS idx_address_location ON address USING GIST (location);
