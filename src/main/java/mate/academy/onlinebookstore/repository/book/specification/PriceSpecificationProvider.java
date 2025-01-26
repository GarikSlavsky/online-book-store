package mate.academy.onlinebookstore.repository.book.specification;

import jakarta.persistence.criteria.Path;
import java.math.BigDecimal;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getDeterminer() {
        return "price";
    }

    @Override
    public Specification<Book> getSpecification(Object... params) {
        BigDecimal priceFrom = (BigDecimal) params[0];
        BigDecimal priceTo = (BigDecimal) params[1];

        return (root, query, criteriaBuilder) -> {
            Path<BigDecimal> price = root.get("price");
            return criteriaBuilder.between(price, priceFrom, priceTo);
        };
    }
}
