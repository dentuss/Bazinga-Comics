ALTER TABLE cart_items
    ADD COLUMN purchase_type ENUM('ORIGINAL','DIGITAL') NOT NULL DEFAULT 'ORIGINAL',
    ADD COLUMN unit_price DECIMAL(10,2) NOT NULL DEFAULT 0;

UPDATE cart_items ci
    JOIN comics c ON ci.comic_id = c.id
    SET ci.unit_price = COALESCE(c.price, 0);

ALTER TABLE cart_items
    DROP INDEX uk_cart_items_cart_comic,
    ADD CONSTRAINT uk_cart_items_cart_comic_type UNIQUE (cart_id, comic_id, purchase_type);
