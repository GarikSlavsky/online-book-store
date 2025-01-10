package mate.academy.onlinebookstore.service;

import java.util.List;
import mate.academy.onlinebookstore.model.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book saveBook(Book book);

    List<Book> findAllBooks();
}
