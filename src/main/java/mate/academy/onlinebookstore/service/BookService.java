package mate.academy.onlinebookstore.service;

import java.util.List;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookSearchParametersDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;

public interface BookService {
    BookDto saveBook(CreateBookRequestDto bookRequestDto);

    BookDto getBookById(Long id);

    List<BookDto> findAllBooks();

    BookDto updateBookById(CreateBookRequestDto bookRequestDto, Long id);

    void deleteBook(Long id);

    List<BookDto> searchBook(BookSearchParametersDto searchParametersDto);
}
