INSERT INTO users (id, email, password, first_name, last_name) VALUES (13, 'test@example.com', 'securePassword123', 'John', 'Doe');
INSERT INTO users_roles (user_id, role_id) VALUES (13, 1);
INSERT INTO shopping_carts (user_id) VALUES (13);
