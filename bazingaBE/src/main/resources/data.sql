INSERT INTO categories (name, description) VALUES ('Avengers', 'Earth''s Mightiest Heroes');
INSERT INTO categories (name, description) VALUES ('X-Men', 'Mutant heroes');
INSERT INTO categories (name, description) VALUES ('Spider-Man', 'Friendly neighborhood');

INSERT INTO conditions (condition_description) VALUES ('New');
INSERT INTO conditions (condition_description) VALUES ('Digital');

INSERT INTO comics (title, author, description, published_year, condition_id, category_id, price, image, created_at, updated_at) VALUES
('X-MEN: AGE OF MYTH ACTION FIGURE (2025) #1', 'Kindt, Unzueta', 'Epic mutant adventure', 2025, 1, 2, 5.99, 'https://picsum.photos/seed/comic1/400/600', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMPIRES OF VIOLENCE (2025) #1', 'Remender, Kim', 'Galaxy spanning saga', 2025, 2, 1, 6.99, 'https://picsum.photos/seed/comic2/400/600', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SPIDER-MAN NOIR (2025) #1', 'Grayson, Mandrake', 'Noir take on Spider-Man', 2025, 2, 3, 4.99, 'https://picsum.photos/seed/comic3/400/600', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('NEW AVENGERS (2025) #1', 'Ahmed, Mora', 'New team rises', 2025, 1, 1, 5.49, 'https://picsum.photos/seed/comic4/400/600', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CAPTAIN AMERICA (2025) #1', 'Thompson, Lee', 'Shield returns', 2025, 2, 1, 5.99, 'https://picsum.photos/seed/comic5/400/600', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
