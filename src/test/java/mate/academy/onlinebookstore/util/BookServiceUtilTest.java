package mate.academy.onlinebookstore.util;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;

public class BookServiceUtilTest {
    private static final Long ACTUAL_ID = 1L;
    private final Category category;

    public BookServiceUtilTest(Category category) {
        this.category = category;
    }

    public CreateBookRequestDto initializeCreateBookRequestDto() {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("Title");
        dto.setAuthor("Author");
        dto.setIsbn("ISBN");
        dto.setPrice(BigDecimal.valueOf(20.00));
        dto.setCategoryIds(List.of(ACTUAL_ID));
        return dto;
    }

    public Book initializeBook() {
        Book book = new Book();
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("ISBN");
        book.setPrice(BigDecimal.valueOf(20.99));
        book.getCategories().add(category);
        return book;
    }

    public Book initializeBookByRequestDto(CreateBookRequestDto requestDto) {
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.getCategories().add(category);
        return book;
    }

    public BookDto initializeBookDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(ACTUAL_ID);
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setCategoryIds(List.of(ACTUAL_ID));
        return dto;
    }
}
