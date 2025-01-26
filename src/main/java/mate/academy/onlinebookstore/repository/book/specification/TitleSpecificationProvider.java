package mate.academy.onlinebookstore.repository.book.specification;

import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getDeterminer() {
        return "title";
    }

    @Override
    public Specification<Book> getSpecification(Object... params) {
        return (root, query, criteriaBuilder) -> root
                .get("title")
                .in(params);
    }
}
