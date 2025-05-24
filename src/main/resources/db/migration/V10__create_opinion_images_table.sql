CREATE TABLE opinion_images (
    opinion_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    CONSTRAINT fk_opinion_images_opinion FOREIGN KEY (opinion_id) REFERENCES opinions(id) ON DELETE CASCADE
);