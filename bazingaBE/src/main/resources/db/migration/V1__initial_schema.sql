-- Core users table
CREATE TABLE users (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(100) NOT NULL,
    email VARCHAR2(255) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    first_name VARCHAR2(100),
    last_name VARCHAR2(100),
    date_of_birth DATE,
    role VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE categories (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(150) NOT NULL,
    description VARCHAR2(500),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE conditions (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    condition_description VARCHAR2(150) NOT NULL,
    CONSTRAINT uk_conditions_description UNIQUE (condition_description)
);

CREATE TABLE comics (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR2(255) NOT NULL,
    author VARCHAR2(255),
    isbn VARCHAR2(30),
    description VARCHAR2(2000),
    published_year NUMBER(4),
    condition_id NUMBER,
    category_id NUMBER,
    price NUMBER(10,2),
    image VARCHAR2(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_comics_isbn UNIQUE (isbn),
    CONSTRAINT fk_comics_condition FOREIGN KEY (condition_id) REFERENCES conditions(id),
    CONSTRAINT fk_comics_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE carts (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_carts_user UNIQUE (user_id)
);

CREATE TABLE cart_items (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cart_id NUMBER NOT NULL,
    comic_id NUMBER NOT NULL,
    quantity NUMBER NOT NULL,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT uk_cart_items_cart_comic UNIQUE (cart_id, comic_id)
);

CREATE TABLE wishlists (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE wishlist_items (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    wishlist_id NUMBER NOT NULL,
    comic_id NUMBER NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_wishlist_items_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_items_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT uk_wishlist_items_wishlist_comic UNIQUE (wishlist_id, comic_id)
);

CREATE TABLE orders (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    total_amount NUMBER(10,2),
    status VARCHAR2(50),
    shipping_address VARCHAR2(500),
    billing_address VARCHAR2(500),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE reviews (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    comic_id NUMBER NOT NULL,
    rating NUMBER NOT NULL,
    comment VARCHAR2(2000),
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE report_categories (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR2(150) NOT NULL,
    description VARCHAR2(500),
    CONSTRAINT uk_report_categories_name UNIQUE (category_name)
);

CREATE TABLE reports (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    report_text VARCHAR2(2000),
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_id NUMBER NOT NULL,
    comic_id NUMBER NOT NULL,
    report_category_id NUMBER NOT NULL,
    CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reports_comic FOREIGN KEY (comic_id) REFERENCES comics(id),
    CONSTRAINT fk_reports_category FOREIGN KEY (report_category_id) REFERENCES report_categories(id)
);

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
