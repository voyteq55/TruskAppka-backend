ALTER TABLE stands ADD COLUMN longitude_tmp DOUBLE PRECISION NOT NULL DEFAULT 0.0;
ALTER TABLE stands ADD COLUMN latitude_tmp DOUBLE PRECISION NOT NULL DEFAULT 0.0;

UPDATE stands
SET
    longitude_tmp = longitude,
    latitude_tmp = latitude;

ALTER TABLE stands DROP COLUMN longitude;
ALTER TABLE stands DROP COLUMN latitude;

ALTER TABLE stands RENAME COLUMN longitude_tmp TO longitude;
ALTER TABLE stands RENAME COLUMN latitude_tmp TO latitude;
