-- Insert books into the books table
INSERT INTO books (id, title, author, isbn, price, description)
VALUES (1, 'Book 1', 'Author 1', 'ISBN 1', 20.00, 'Description of Book 1'),
       (2, 'Book 2', 'Author 2', 'ISBN 2', 25.00, 'Description of Book 2');

-- Insert relationships into the book_categories table
INSERT INTO books_categories (book_id, category_id) VALUES (1, 1), (1, 2), (2, 1);
