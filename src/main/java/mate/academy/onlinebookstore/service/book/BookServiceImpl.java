package mate.academy.onlinebookstore.service.book;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.onlinebookstore.dto.book.BookSearchParametersDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.book.specification.BookSpecificationBuilder;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto saveBook(CreateBookRequestDto bookRequestDto) {
        Book book = bookMapper.intoModel(bookRequestDto);
        Set<Category> categorySet = bookRequestDto.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Category with id " + categoryId + " not found."
                        )
                ))
                .collect(Collectors.toSet());

        book.setCategories(categorySet);
        return bookMapper.intoBookDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id: " + id + " not found.")
        );
        return bookMapper.intoBookDto(book);
    }

    @Override
    public List<BookDto> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::intoBookDto)
                .toList();
    }

    @Override
    public BookDto updateBookById(CreateBookRequestDto bookRequestDto, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id: " + id + " not found.")
        );

        Set<Category> categorySet = bookRequestDto.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Category with id " + categoryId + " not found."
                        )
                ))
                .collect(Collectors.toSet());

        book.setCategories(categorySet);
        bookMapper.updateModelFromDto(bookRequestDto, book);
        return bookMapper.intoBookDto(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> searchBook(
            BookSearchParametersDto searchParametersDto, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParametersDto);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::intoBookDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            Long categoryId, Pageable pageable) {

        return bookRepository.findAllByCategoryId(categoryId, pageable).stream()
                .map(bookMapper::intoBookDtoWithoutCategories)
                .toList();
    }
}
