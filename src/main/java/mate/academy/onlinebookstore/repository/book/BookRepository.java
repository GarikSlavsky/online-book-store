package mate.academy.onlinebookstore.repository.book;

import java.util.List;
import mate.academy.onlinebookstore.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query(value = "SELECT * "
                 + "FROM books "
                 + "INNER JOIN books_categories "
                 + "ON books.id = books_categories.book_id "
                 + "WHERE books_categories.category_id = :categoryId", nativeQuery = true)
    List<Book> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
}
