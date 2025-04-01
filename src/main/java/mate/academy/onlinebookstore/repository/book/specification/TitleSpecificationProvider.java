package mate.academy.onlinebookstore.repository.book.specification;

import static mate.academy.onlinebookstore.dto.book.BookSearchParametersDto.TITLE;

import jakarta.persistence.criteria.JoinType;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getDeterminer() {
        return TITLE;
    }

    @Override
    public Specification<Book> getSpecification(Object... params) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("categories", JoinType.LEFT);
            return root.get(TITLE)
                    .in(params);
        };
    }
}
