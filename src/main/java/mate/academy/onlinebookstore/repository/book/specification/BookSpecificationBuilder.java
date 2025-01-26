package mate.academy.onlinebookstore.repository.book.specification;

import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.ParameterSearcher;
import mate.academy.onlinebookstore.dto.book.BookSearchParametersDto;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationBuilder;
import mate.academy.onlinebookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(ParameterSearcher parameters) {
        Specification<Book> spec = Specification.where(null);
        BookSearchParametersDto bookParams = (BookSearchParametersDto) parameters;

        if (parameterValidator(bookParams.title())) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(bookParams.title()));
        }
        if (parameterValidator(bookParams.author())) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(bookParams.author()));
        }
        if (bookParams.priceGreaterThan() != null && bookParams.priceLessThan() != null) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider("price")
                    .getSpecification(bookParams.priceGreaterThan(), bookParams.priceLessThan()));
        }
        if (parameterValidator(bookParams.description())) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider("description")
                    .getSpecification(bookParams.description()));
        }

        return spec;
    }

    private boolean parameterValidator(String bookParams) {
        return bookParams != null && !bookParams.isEmpty();
    }
}
