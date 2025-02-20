package mate.academy.onlinebookstore.repository.shoppingcart;

import java.util.Optional;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<ShoppingCart, Long> {

    @EntityGraph(attributePaths = "cartItems")
    Optional<ShoppingCart> findById(Long customerId);
}
