package mate.academy.onlinebookstore.repository;

import mate.academy.onlinebookstore.dto.ParameterSearcher;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    public Specification<T> build(ParameterSearcher parameters);
}
