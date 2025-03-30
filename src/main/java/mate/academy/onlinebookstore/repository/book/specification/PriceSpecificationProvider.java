package mate.academy.onlinebookstore.repository.book.specification;

import static mate.academy.onlinebookstore.dto.book.BookSearchParametersDto.PRICE;

import jakarta.persistence.criteria.JoinType;
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
        return PRICE;
    }

    @Override
    public Specification<Book> getSpecification(Object... params) {
        BigDecimal priceFrom = (BigDecimal) params[0];
        BigDecimal priceTo = (BigDecimal) params[1];

        return (root, query, criteriaBuilder) -> {
            root.fetch("categories", JoinType.LEFT);
            Path<BigDecimal> price = root.get(PRICE);
            return criteriaBuilder.between(price, priceFrom, priceTo);
        };
    }
}
