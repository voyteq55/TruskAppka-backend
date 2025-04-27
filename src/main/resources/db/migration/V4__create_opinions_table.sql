CREATE TABLE opinions (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    quality_rating DECIMAL(10, 2) NOT NULL,
    service_rating DECIMAL(10, 2) NOT NULL,
    price_rating DECIMAL(10, 2) NOT NULL,
    comment VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    stand_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (stand_id) REFERENCES stands(id) ON DELETE CASCADE
);