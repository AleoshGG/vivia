-- Fix postal_code column type from CHAR(5) to VARCHAR(5)
-- This resolves the schema validation error:
-- "Schema-validation: wrong column type encountered in column [postal_code] in table [neighborhood];
--  found [bpchar (Types#CHAR)], but expecting [varchar(5) (Types#VARCHAR)]"

ALTER TABLE neighborhood
ALTER COLUMN postal_code TYPE VARCHAR(5);
