-- Drop tables if they exist
DROP TABLE IF EXISTS books_categories;

CREATE TABLE books_categories (
                                 book_id INT NOT NULL,
                                 category_id INT NOT NULL
);

