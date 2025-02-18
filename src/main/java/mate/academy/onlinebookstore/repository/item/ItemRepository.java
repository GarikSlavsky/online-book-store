package mate.academy.onlinebookstore.repository.item;

import mate.academy.onlinebookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<CartItem, Long> {
}
