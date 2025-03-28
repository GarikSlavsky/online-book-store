package mate.academy.onlinebookstore.repository.book.specification;

import static mate.academy.onlinebookstore.dto.book.BookSearchParametersDto.DESCRIPTION;

import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DescriptionSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getDeterminer() {
        return DESCRIPTION;
    }

    @Override
    public Specification<Book> getSpecification(Object... params) {
        String description = (String) params[0];
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                root.get(DESCRIPTION), "%" + description + "%"
        );
    }
}
