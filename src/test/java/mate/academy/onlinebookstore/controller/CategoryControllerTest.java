package mate.academy.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.dto.category.CreateCategoryRequestDto;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
        CreateCategoryRequestDto requestDto = initializeCreateCategoryRequestDto();

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(1L);
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

        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Create category without ADMIN role should return Forbidden.")
    void createCategory_NoAdminRole_Forbidden() throws Exception {
        CreateCategoryRequestDto requestDto = initializeCreateCategoryRequestDto();

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/categories")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create category with invalid name should return Bad Request.")
    void createCategory_InvalidName_BadRequest() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("123InvalidName");
        requestDto.setDescription("A valid description.");

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/categories")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all categories.")
    void getAll_GivenCategories_ReturnListOfCategoryDto() throws Exception {
        List<CategoryResponseDto> expected = getCategoryResponseDtoList();

        MvcResult result = mockMvc.perform(
                get("/categories").contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryResponseDto[].class);

        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Det category by ID.")
    void getCategoryById_Ok() throws Exception {
        CategoryResponseDto expected = initializeCategoryResponseDto();

        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class);

        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual));
    }

    private static List<CategoryResponseDto> getCategoryResponseDtoList() {
        final CategoryResponseDto responseDto1 = initializeCategoryResponseDto();

        final CategoryResponseDto responseDto2 = new CategoryResponseDto();
        responseDto2.setId(2L);
        responseDto2.setName("Fantasy");
        responseDto2.setDescription("Stories that involve magic or other supernatural elements.");

        List<CategoryResponseDto> expected = new ArrayList<>();
        expected.add(responseDto1);
        expected.add(responseDto2);
        return expected;
    }

    private static CategoryResponseDto initializeCategoryResponseDto() {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(1L);
        dto.setName("Literary fiction");
        dto.setDescription("A comprehensive guide to best practices in literary fiction.");
        return dto;
    }

    private static CreateCategoryRequestDto initializeCreateCategoryRequestDto() {
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
        dto.setName("Literary fiction");
        dto.setDescription("A comprehensive guide to best practices in literary fiction.");
        return dto;
    }
}
