ALTER TABLE opinions ADD COLUMN quality_rating_int INTEGER NOT NULL DEFAULT 0;
ALTER TABLE opinions ADD COLUMN service_rating_int INTEGER NOT NULL DEFAULT 0;
ALTER TABLE opinions ADD COLUMN price_rating_int INTEGER NOT NULL DEFAULT 0;

UPDATE opinions
SET
    quality_rating_int = ROUND(quality_rating),
    service_rating_int = ROUND(service_rating),
    price_rating_int = ROUND(price_rating);

ALTER TABLE opinions DROP COLUMN quality_rating;
ALTER TABLE opinions DROP COLUMN service_rating;
ALTER TABLE opinions DROP COLUMN price_rating;

ALTER TABLE opinions RENAME COLUMN quality_rating_int TO quality_rating;
ALTER TABLE opinions RENAME COLUMN service_rating_int TO service_rating;
ALTER TABLE opinions RENAME COLUMN price_rating_int TO price_rating;