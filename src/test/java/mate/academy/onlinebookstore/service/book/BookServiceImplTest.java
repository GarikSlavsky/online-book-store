package mate.academy.onlinebookstore.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import mate.academy.onlinebookstore.util.BookServiceUtilTest;
import org.junit.jupiter.api.BeforeAll;
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
    private static final Long NON_EXISTENT_ID = 999L;
    private static final Long ACTUAL_ID = 1L;
    private static Category category;
    private static BookServiceUtilTest bookServiceUtilTest;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeAll
    static void beforeAll() {
        category = new Category();
        category.setId(ACTUAL_ID);
        category.setName("Category");
        bookServiceUtilTest = new BookServiceUtilTest(category);
    }

    @Test
    @DisplayName("Verify saveBook method works properly.")
    void saveBook_ValidRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = bookServiceUtilTest.initializeCreateBookRequestDto();

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.getCategories().add(category);

        BookDto expected = new BookDto();

        when(bookMapper.intoModel(requestDto)).thenReturn(book);
        when(categoryRepository.findById(ACTUAL_ID)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.intoBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.saveBook(requestDto);
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Verify updating a book entity.")
    void updateBook_ValidRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = bookServiceUtilTest.initializeCreateBookRequestDto();
        requestDto.setTitle("New title");
        requestDto.setPrice(BigDecimal.valueOf(25.00));

        Book book = bookServiceUtilTest.initializeBookByRequestDto(requestDto);
        BookDto expected = bookServiceUtilTest.initializeBookDto(book);

        when(bookRepository.findById(ACTUAL_ID)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(ACTUAL_ID)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.intoBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.updateBookById(requestDto, ACTUAL_ID);
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository).save(book);
        verify(bookRepository).findById(ACTUAL_ID);
    }

    @Test
    @DisplayName("Get a list of books.")
    void getAllBooks_ReturnsBookDtoList() {
        final Book book1 = bookServiceUtilTest.initializeBook();
        final Book book2 = new Book();
        book2.setTitle("Title 2");
        book2.setAuthor("Author 2");
        book2.setIsbn("ISBN 2");
        book2.setPrice(BigDecimal.valueOf(25.00));
        book2.getCategories().add(category);

        BookDto bookDto1 = bookServiceUtilTest.initializeBookDto(book1);
        BookDto bookDto2 = bookServiceUtilTest.initializeBookDto(book2);
        bookDto2.setId(2L);

        List<BookDto> expected = List.of(bookDto1, bookDto2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book1, book2));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.intoBookDto(book1)).thenReturn(bookDto1);
        when(bookMapper.intoBookDto(book2)).thenReturn(bookDto2);

        List<BookDto> actual = bookService.findAllBooks(pageable);
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Verify saveBook method throws exception for invalid input.")
    void saveBook_InvalidRequestDto_ThrowsException() {
        CreateBookRequestDto requestDto = bookServiceUtilTest.initializeCreateBookRequestDto();
        requestDto.setTitle(null);
        Book book = bookServiceUtilTest.initializeBookByRequestDto(requestDto);

        when(bookMapper.intoModel(requestDto)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> bookService.saveBook(requestDto));
        verify(bookRepository, never()).save(book);
    }

    @Test
    @DisplayName("Verify saveBook method throws exception for non-existent category.")
    void saveBook_NonExistentCategoryId_ThrowsEntityNotFoundException() {
        CreateBookRequestDto requestDto = bookServiceUtilTest.initializeCreateBookRequestDto();
        requestDto.setCategoryIds(List.of(NON_EXISTENT_ID));
        Book book = bookServiceUtilTest.initializeBookByRequestDto(requestDto);

        when(categoryRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class, () -> bookService.saveBook(requestDto));
        assertThat(thrown.getMessage()).isEqualTo("Category with id 999 not found.");
        verify(bookRepository, never()).save(book);
    }
}
