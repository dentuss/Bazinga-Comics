ALTER TABLE comics
    ADD COLUMN comic_type VARCHAR(20) NOT NULL DEFAULT 'PHYSICAL_COPY';

CREATE TABLE libraries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_libraries_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_libraries_user UNIQUE (user_id)
);

CREATE TABLE library_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    library_id BIGINT NOT NULL,
    comic_id BIGINT NOT NULL,
    added_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_library_items_library FOREIGN KEY (library_id) REFERENCES libraries(id),
    CONSTRAINT fk_library_items_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT uk_library_items_library_comic UNIQUE (library_id, comic_id)
);

CREATE INDEX idx_library_items_comic ON library_items(comic_id);
