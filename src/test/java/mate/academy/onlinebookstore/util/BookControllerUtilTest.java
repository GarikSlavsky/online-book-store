package mate.academy.onlinebookstore.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;

public class BookControllerUtilTest {
    public CreateBookRequestDto initializeCreateBookRequestDtoForCreateMethod() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Effective Java");
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setIsbn("978-0134685991");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setDescription("A comprehensive guide to best practices in Java programming.");
        requestDto.setCategoryIds(List.of(1L, 2L));
        return requestDto;
    }

    public BookDto initializeBookDtoForCreateMethod(CreateBookRequestDto requestDto) {
        BookDto dto = new BookDto();
        dto.setTitle(requestDto.getTitle());
        dto.setAuthor(requestDto.getAuthor());
        dto.setIsbn(requestDto.getIsbn());
        dto.setPrice(requestDto.getPrice());
        dto.setDescription(requestDto.getDescription());
        dto.setCategoryIds(requestDto.getCategoryIds());
        return dto;
    }

    public BookDto initializeBookDto() {
        BookDto dto = new BookDto();
        dto.setId(1L);
        dto.setTitle("Book 1");
        dto.setAuthor("Author 1");
        dto.setIsbn("ISBN 1");
        dto.setPrice(BigDecimal.valueOf(20.00));
        dto.setDescription("Description of Book 1");
        dto.setCategoryIds(List.of(1L, 2L));
        return dto;
    }

    public List<BookDto> getBookResponseDtolist() {
        final BookDto responseDto1 = initializeBookDto();

        final BookDto responseDto2 = new BookDto();
        responseDto2.setId(2L);
        responseDto2.setTitle("Book 2");
        responseDto2.setAuthor("Author 2");
        responseDto2.setIsbn("ISBN 2");
        responseDto2.setPrice(BigDecimal.valueOf(25.00));
        responseDto2.setDescription("Description of Book 2");
        responseDto2.setCategoryIds(List.of(1L));

        List<BookDto> expected = new ArrayList<>();
        expected.add(responseDto1);
        expected.add(responseDto2);
        return expected;
    }

    public List<Long> sortCategoryIds(BookDto dto) {
        return dto.getCategoryIds()
                .stream()
                .sorted(Long::compare)
                .toList();
    }
}
