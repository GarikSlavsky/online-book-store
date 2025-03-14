package mate.academy.onlinebookstore.repository;

import java.util.List;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/book/create-table-books_categories.sql",
        "classpath:database/category/add-two-categories.sql",
        "classpath:database/book/add-two-books.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/category/remove-all-categories.sql",
        "classpath:database/book/remove-all-books.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Get empty list of books by nonexistent category ID.")
    @Sql(scripts = "classpath:database/category/add-unused-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllBooksByCategoryId_NonExistentCategoryId_ReturnsEmptyList() {
        List<Book> actual = bookRepository.findAllByCategoryId(3L, pageable);
        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find all books by category ID.")
    void findAllBooksByCategoryId_ExistentCategoryId_ReturnsAllBooks() {
        List<Book> actual = bookRepository.findAllByCategoryId(1L, pageable);
        Assertions.assertEquals(2, actual.size());
    }
}
