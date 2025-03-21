package mate.academy.onlinebookstore.util;

import java.util.List;
import mate.academy.onlinebookstore.dto.item.ItemResponseDto;
import mate.academy.onlinebookstore.dto.shoppingcart.CartResponseDto;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CartControllerUtilTest {
    private static final Long ACTUAL_USER_ID = 13L;

    public CartResponseDto getCartResponseDto() {
        ItemResponseDto responseDto1 =
                new ItemResponseDto(772L, 36L, "Book 1", 2);
        ItemResponseDto responseDto2 =
                new ItemResponseDto(773L, 23L, "Book 2", 1);
        ItemResponseDto responseDto3 =
                new ItemResponseDto(774L, 105L, "Book 3", 3);

        CartResponseDto expected = new CartResponseDto();
        expected.setId(ACTUAL_USER_ID);
        expected.setUserId(ACTUAL_USER_ID);
        expected.setCartItems(List.of(responseDto1, responseDto2, responseDto3));
        return expected;
    }

    public void authenticateUser() {
        User testUser = new User();
        testUser.setId(ACTUAL_USER_ID);
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);
        testUser.getRoles().add(role);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser,
                null,
                testUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
