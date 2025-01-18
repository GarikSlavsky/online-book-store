package mate.academy.onlinebookstore.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public BookDto saveBook(CreateBookRequestDto bookRequestDto) {
        Book book = bookMapper.intoModel(bookRequestDto);
        return bookMapper.intoBookDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id" + id + " not found.")
        );
        return bookMapper.intoBookDto(book);
    }

    @Override
    public List<BookDto> getByAuthor(String authorName) {
        return bookRepository.findByAuthor(authorName).stream()
                .map(bookMapper::intoBookDto)
                .toList();
    }

    @Override
    public List<BookDto> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::intoBookDto)
                .toList();
    }
}
