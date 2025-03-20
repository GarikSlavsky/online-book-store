INSERT INTO books (id, title, author, isbn, price, description)
VALUES (36, 'Book 1', 'Author 1', 'ISBN 1', 20.00, 'Description of Book 1'),
       (23, 'Book 2', 'Author 2', 'ISBN 2', 25.00, 'Description of Book 2'),
       (105, 'Book 3', 'Author 2', 'ISBN 3', 29.99, 'Description of Book 3');

INSERT INTO items (id, cart_id, book_id, quantity) VALUES (772, 13, 36, 2), (773, 13, 23, 1);
