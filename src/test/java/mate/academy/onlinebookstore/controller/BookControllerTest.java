package mate.academy.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.service.book.BookService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final long NON_EXISTENT_ID = 999L;
    private static final String EXCEPTION_MESSAGE =
            "Book with id: " + NON_EXISTENT_ID + " not found.";

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private BookService bookService;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/category/add-two-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/add-two-books.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/remove-all-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/category/remove-all-categories.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Save new book.")
    void createBook_ValidRequestDto_Ok() throws Exception {
        CreateBookRequestDto requestDto = initializeCreateBookRequestDtoForCreateMethod();
        BookDto expected = initializeBookDtoForCreateMethod(requestDto);

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertEquals(sortCategoryIds(expected), sortCategoryIds(actual));
        assertTrue(reflectionEquals(expected, actual, "id", "price", "categoryIds"));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books.")
    void findAll_GivenBooks_ReturnListOfBookDto() throws Exception {
        List<BookDto> expected = getBookResponseDtolist();

        MvcResult result = mockMvc.perform(
                        get("/books").contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);

        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(sortCategoryIds(expected.get(i)), sortCategoryIds(actual[i]));
            assertTrue(reflectionEquals(
                    expected.get(i), actual[i],"price", "categoryIds"));
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID.")
    void getBookById_Ok() throws Exception {
        BookDto expected = initializeBookDto();

        MvcResult result = mockMvc.perform(
                        get("/books/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertEquals(sortCategoryIds(expected), sortCategoryIds(actual));
        assertTrue(reflectionEquals(expected, actual, "price", "categoryIds"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get book by ID - Nonexistent ID throws exception.")
    void getBookById_NonExistentId_ThrowsEntityNotFoundException() throws Exception {
        when(bookService.getBookById(NON_EXISTENT_ID))
                .thenThrow(new EntityNotFoundException(EXCEPTION_MESSAGE));

        mockMvc.perform(get("/books/{id}", NON_EXISTENT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(
                        EntityNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(
                        EXCEPTION_MESSAGE,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    private static CreateBookRequestDto initializeCreateBookRequestDtoForCreateMethod() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Effective Java");
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setIsbn("978-0134685991");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setDescription("A comprehensive guide to best practices in Java programming.");
        requestDto.setCategoryIds(List.of(1L, 2L));
        return requestDto;
    }

    private static BookDto initializeBookDtoForCreateMethod(CreateBookRequestDto requestDto) {
        BookDto dto = new BookDto();
        dto.setTitle(requestDto.getTitle());
        dto.setAuthor(requestDto.getAuthor());
        dto.setIsbn(requestDto.getIsbn());
        dto.setPrice(requestDto.getPrice());
        dto.setDescription(requestDto.getDescription());
        dto.setCategoryIds(requestDto.getCategoryIds());
        return dto;
    }

    private static BookDto initializeBookDto() {
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

    private static @NotNull List<BookDto> getBookResponseDtolist() {
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

    private static List<Long> sortCategoryIds(BookDto dto) {
        return dto.getCategoryIds()
                .stream()
                .sorted(Long::compare)
                .toList();
    }
}
