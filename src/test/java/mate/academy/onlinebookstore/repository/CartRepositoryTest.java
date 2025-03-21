package mate.academy.onlinebookstore.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.repository.shoppingcart.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/shopping_cart/add-shopping_cart.sql",
        "classpath:database/item/add-two-items.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/item/remove-all-items.sql",
        "classpath:database/shopping_cart/remove-all-shopping_carts.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class CartRepositoryTest {
    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Find Shopping Cart by User ID - Existent Shopping Cart")
    void findByUserId_ExistentShoppingCart_ReturnOptional() {
        Optional<ShoppingCart> actual = cartRepository.findByUserId(13L);
        assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find Shopping Cart by User ID - Nonexistent User")
    void findByUserId_NonexistentUser_ReturnEmptyOptional() {
        Optional<ShoppingCart> actual = cartRepository.findByUserId(999L);
        assertFalse(actual.isPresent());
    }
}
