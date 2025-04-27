CREATE TABLE opinions_tags (
    opinion_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (opinion_id, tag_id),
    CONSTRAINT fk_opinion FOREIGN KEY (opinion_id) REFERENCES opinions(id) ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);