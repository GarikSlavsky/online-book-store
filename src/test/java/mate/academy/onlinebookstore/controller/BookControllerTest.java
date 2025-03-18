package mate.academy.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.util.BookControllerUtilTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    private static BookControllerUtilTest bookControllerUtilTest;
    private static final Long ACTUAL_ID = 1L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final String EXCEPTION_MESSAGE =
            "Book with id: " + NON_EXISTENT_ID + " not found.";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {

        bookControllerUtilTest = new BookControllerUtilTest();
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
        CreateBookRequestDto requestDto =
                bookControllerUtilTest.initializeCreateBookRequestDtoForCreateMethod();
        BookDto expected = bookControllerUtilTest.initializeBookDtoForCreateMethod(requestDto);

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
        assertEquals(
                bookControllerUtilTest.sortCategoryIds(expected),
                bookControllerUtilTest.sortCategoryIds(actual)
        );
        assertTrue(reflectionEquals(expected, actual, "id", "price", "categoryIds"));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books.")
    void findAll_GivenBooks_ReturnListOfBookDto() throws Exception {
        List<BookDto> expected = bookControllerUtilTest.getBookResponseDtolist();

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
            assertEquals(
                    bookControllerUtilTest.sortCategoryIds(expected.get(i)),
                    bookControllerUtilTest.sortCategoryIds(actual[i])
            );
            assertTrue(reflectionEquals(
                    expected.get(i), actual[i],"price", "categoryIds"));
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID.")
    void getBookById_Ok() throws Exception {
        BookDto expected = bookControllerUtilTest.initializeBookDto();

        MvcResult result = mockMvc.perform(
                        get("/books/{id}", ACTUAL_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertEquals(
                bookControllerUtilTest.sortCategoryIds(expected),
                bookControllerUtilTest.sortCategoryIds(actual)
        );
        assertTrue(reflectionEquals(expected, actual, "price", "categoryIds"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get book by ID - Nonexistent ID throws exception.")
    void getBookById_NonExistentId_ThrowsEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/books/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(
                        EntityNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(
                        EXCEPTION_MESSAGE,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
