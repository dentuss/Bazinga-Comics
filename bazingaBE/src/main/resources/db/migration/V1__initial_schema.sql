-- Core users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE conditions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    condition_description VARCHAR(150) NOT NULL,
    CONSTRAINT uk_conditions_description UNIQUE (condition_description)
);

CREATE TABLE comics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    isbn VARCHAR(30),
    description TEXT,
    published_year INT,
    condition_id BIGINT,
    category_id BIGINT,
    price DECIMAL(10,2),
    image VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_comics_isbn UNIQUE (isbn),
    CONSTRAINT fk_comics_condition FOREIGN KEY (condition_id) REFERENCES conditions(id),
    CONSTRAINT fk_comics_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_carts_user UNIQUE (user_id)
);

CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    comic_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT uk_cart_items_cart_comic UNIQUE (cart_id, comic_id)
);

CREATE TABLE wishlists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE wishlist_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wishlist_id BIGINT NOT NULL,
    comic_id BIGINT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_wishlist_items_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_items_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT uk_wishlist_items_wishlist_comic UNIQUE (wishlist_id, comic_id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2),
    status VARCHAR(50),
    shipping_address VARCHAR(500),
    billing_address VARCHAR(500),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    comic_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE report_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    CONSTRAINT uk_report_categories_name UNIQUE (category_name)
);

CREATE TABLE reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_text TEXT,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    comic_id BIGINT NOT NULL,
    report_category_id BIGINT NOT NULL,
    CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reports_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT fk_reports_category FOREIGN KEY (report_category_id) REFERENCES report_categories(id)
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

CREATE TABLE libraries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_libraries_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_libraries_user UNIQUE (user_id)
);

ALTER TABLE comics
    ADD COLUMN comic_type ENUM('ONLY_DIGITAL','PHYSICAL_COPY') NOT NULL;

CREATE INDEX idx_comics_category ON comics(category_id);
CREATE INDEX idx_comics_condition ON comics(condition_id);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_cart_items_comic ON cart_items(comic_id);
CREATE INDEX idx_wishlist_items_wishlist ON wishlist_items(wishlist_id);
CREATE INDEX idx_wishlist_items_comic ON wishlist_items(comic_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_comic ON reviews(comic_id);
CREATE INDEX idx_reports_user ON reports(user_id);
CREATE INDEX idx_reports_comic ON reports(comic_id);
CREATE INDEX idx_reports_category ON reports(report_category_id);
CREATE INDEX idx_library_items_comic ON library_items(comic_id);
