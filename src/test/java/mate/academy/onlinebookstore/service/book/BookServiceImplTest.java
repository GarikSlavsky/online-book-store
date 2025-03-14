package mate.academy.onlinebookstore.service.book;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Category");
    }

    @Test
    @DisplayName("Verify saveBook method works properly.")
    void saveBook_ValidRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Title");
        requestDto.setAuthor("Author");
        requestDto.setIsbn("ISBN");
        requestDto.setPrice(BigDecimal.valueOf(20.00));
        requestDto.setCategoryIds(List.of(1L));

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.getCategories().add(category);

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(book.getTitle());
        expected.setAuthor(book.getAuthor());
        expected.setIsbn(book.getIsbn());
        expected.setPrice(book.getPrice());
        expected.setCategoryIds(List.of(1L));

        when(bookMapper.intoModel(requestDto)).thenReturn(book);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.intoBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.saveBook(requestDto);
        Assertions.assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Verify updating a book entity.")
    void updateBook_ValidRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("New title");
        requestDto.setAuthor("Author");
        requestDto.setIsbn("ISBN");
        requestDto.setPrice(BigDecimal.valueOf(25.00));
        requestDto.setCategoryIds(List.of(1L, 2L));

        Book book = new Book();
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("ISBN");
        book.setPrice(BigDecimal.valueOf(20.99));
        book.getCategories().add(category);

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setCategoryIds(List.of(1L, 2L));

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.intoBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.updateBookById(requestDto, 1L);
        Assertions.assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Get a list of books.")
    void getAllBooks_ReturnsBookDtoList() {
        Book book1 = new Book();
        book1.setTitle("Title 1");
        book1.setAuthor("Author 1");
        book1.setIsbn("ISBN 1");
        book1.setPrice(BigDecimal.valueOf(20.99));
        book1.getCategories().add(category);
        Book book2 = new Book();
        book2.setTitle("Title 2");
        book2.setAuthor("Author 2");
        book2.setIsbn("ISBN 2");
        book2.setPrice(BigDecimal.valueOf(25.00));
        book2.getCategories().add(category);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setTitle(book1.getTitle());
        bookDto1.setAuthor(book1.getAuthor());
        bookDto1.setIsbn(book1.getIsbn());
        bookDto1.setPrice(book1.getPrice());
        bookDto1.setCategoryIds(List.of(1L));
        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle(book2.getTitle());
        bookDto2.setAuthor(book2.getAuthor());
        bookDto2.setIsbn(book2.getIsbn());
        bookDto2.setPrice(book2.getPrice());
        bookDto2.setCategoryIds(List.of(1L));

        List<BookDto> expected = List.of(bookDto1, bookDto2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book1, book2));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.intoBookDto(book1)).thenReturn(bookDto1);
        when(bookMapper.intoBookDto(book2)).thenReturn(bookDto2);

        List<BookDto> actual = bookService.findAllBooks(pageable);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
