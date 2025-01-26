package mate.academy.onlinebookstore.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getDeterminer();

    Specification<T> getSpecification(Object... params);
}
