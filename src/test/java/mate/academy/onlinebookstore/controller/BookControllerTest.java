package mate.academy.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                    new ClassPathResource("database/book/create-table-books_categories.sql")
            );
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
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Effective Java");
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setIsbn("978-0134685991");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setDescription("A comprehensive guide to best practices in Java programming.");
        requestDto.setCategoryIds(List.of(1L, 2L));

        BookDto expected = new BookDto();
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setDescription(requestDto.getDescription());

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

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books.")
    void findAll_GivenBooks_ReturnListOfBookDto() throws Exception {
        List<BookDto> expected = getBookResponseDtos();

        MvcResult result = mockMvc.perform(
                        get("/books").contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.size(), actual.length);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    private static @NotNull List<BookDto> getBookResponseDtos() {
        BookDto responseDto1 = new BookDto();
        responseDto1.setId(1L);
        responseDto1.setTitle("Book 1");
        responseDto1.setAuthor("Author 1");
        responseDto1.setIsbn("ISBN 1");
        responseDto1.setPrice(BigDecimal.valueOf(20.00).stripTrailingZeros());
        responseDto1.setDescription("Description of Book 1");
        responseDto1.setCategoryIds(List.of(1L, 2L));

        BookDto responseDto2 = new BookDto();
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

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID.")
    void getBookById_Ok() throws Exception {
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Book 1");
        expected.setAuthor("Author 1");
        expected.setIsbn("ISBN 1");
        expected.setPrice(BigDecimal.valueOf(20.00));
        expected.setDescription("Description of Book 1");

        MvcResult result = mockMvc.perform(
                        get("/books/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
