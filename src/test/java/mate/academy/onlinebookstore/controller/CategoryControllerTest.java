package mate.academy.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.dto.category.CreateCategoryRequestDto;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
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
                    new ClassPathResource("database/category/add-two-categories.sql")
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
                    new ClassPathResource("database/category/remove-all-categories.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Save new category.")
    @Sql(scripts = "classpath:database/category/delete-fiction-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_Ok() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Literary fiction");
        requestDto.setDescription("A comprehensive guide to best practices in literary fiction.");

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setName(requestDto.getName());
        expected.setDescription(requestDto.getDescription());

        String json = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(
                post("/categories")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryResponseDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all categories.")
    void getAll_GivenCategories_ReturnListOfCategoryDto() throws Exception {
        List<CategoryResponseDto> expected = getCategoryResponseDtos();

        MvcResult result = mockMvc.perform(
                get("/categories").contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryResponseDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    private static @NotNull List<CategoryResponseDto> getCategoryResponseDtos() {
        CategoryResponseDto responseDto1 = new CategoryResponseDto();
        responseDto1.setId(1L);
        responseDto1.setName("Literary fiction");
        responseDto1.setDescription("A comprehensive guide to best practices in literary fiction.");
        CategoryResponseDto responseDto2 = new CategoryResponseDto();
        responseDto2.setId(2L);
        responseDto2.setName("Fantasy");
        responseDto2.setDescription("Stories that involve magic or other supernatural elements.");

        List<CategoryResponseDto> expected = new ArrayList<>();
        expected.add(responseDto1);
        expected.add(responseDto2);
        return expected;
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Det category by ID.")
    void getCategoryById_Ok() throws Exception {
        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(1L);
        expected.setName("Literary fiction");
        expected.setDescription("A comprehensive guide to best practices in literary fiction.");

        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
