package mate.academy.onlinebookstore.service;

import java.util.List;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto saveBook(CreateBookRequestDto bookRequestDto);

    BookDto getBookById(Long id);

    List<BookDto> findAllBooks();
}
