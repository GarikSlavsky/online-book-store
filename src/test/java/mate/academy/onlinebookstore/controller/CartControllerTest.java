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
import java.util.HashSet;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.onlinebookstore.dto.item.ItemAddRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.util.CartControllerUtilTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartControllerTest {
    protected static MockMvc mockMvc;
    private static CartControllerUtilTest cartControllerUtilTest;
    private static final Long BOOK_TO_ADD_ID = 105L;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {

        cartControllerUtilTest = new CartControllerUtilTest();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_cart/add-shopping_cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/item/add-two-items.sql")
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
                    new ClassPathResource("database/item/remove-all-items.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_cart/remove-all-shopping_carts.sql")
            );
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Add book to user's shopping cart.")
    void addBook_ValidRequestDto_Ok() throws Exception {
        ItemAddRequestDto requestDto = new ItemAddRequestDto();
        requestDto.setBookId(BOOK_TO_ADD_ID);
        requestDto.setQuantity(3);

        cartControllerUtilTest.authenticateUser();
        CartResponseDto expected = cartControllerUtilTest.getCartResponseDto();
        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/cart")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();

        CartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartResponseDto.class
        );

        assertNotNull(actual);
        assertEquals(
                new HashSet<>(expected.getCartItems()),
                new HashSet<>(actual.getCartItems())
        );
        assertTrue(reflectionEquals(expected, actual, "cartItems"));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Display the user's shopping cart content.")
    void viewCart_getShoppingCartContent_Ok() throws Exception {
        cartControllerUtilTest.authenticateUser();
        CartResponseDto expected = cartControllerUtilTest.getCartResponseDto();

        MvcResult result = mockMvc.perform(
                        get("/cart").contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartResponseDto.class
        );

        assertNotNull(actual);
        assertEquals(
                new HashSet<>(expected.getCartItems()),
                new HashSet<>(actual.getCartItems())
        );
        assertTrue(reflectionEquals(expected, actual, "cartItems"));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Try to get a content of the cart by wrong user ID.")
    void viewCart_wrongCredential_NotFoundStatus() throws Exception {
        User testUser = new User();
        testUser.setId(999L);
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);
        testUser.getRoles().add(role);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser,
                null,
                testUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/cart").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
