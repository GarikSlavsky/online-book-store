package mate.academy.onlinebookstore.repository.book.specification;

import static mate.academy.onlinebookstore.dto.book.BookSearchParametersDto.AUTHOR;
import static mate.academy.onlinebookstore.dto.book.BookSearchParametersDto.DESCRIPTION;
import static mate.academy.onlinebookstore.dto.book.BookSearchParametersDto.PRICE;
import static mate.academy.onlinebookstore.dto.book.BookSearchParametersDto.TITLE;

import lombok.RequiredArgsConstructor;
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
    public Specification<Book> build(BookSearchParametersDto parameters) {
        Specification<Book> spec = Specification.where(null);

        if (parameterValidator(parameters.title())) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(TITLE)
                    .getSpecification(parameters.title()));
        }
        if (parameterValidator(parameters.author())) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(AUTHOR)
                    .getSpecification(parameters.author()));
        }
        if (parameters.priceGreaterThan() != null && parameters.priceLessThan() != null) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(PRICE)
                    .getSpecification(parameters.priceGreaterThan(), parameters.priceLessThan()));
        }
        if (parameterValidator(parameters.description())) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(DESCRIPTION)
                    .getSpecification(parameters.description()));
        }

        return spec;
    }

    private boolean parameterValidator(String bookParams) {
        return bookParams != null && !bookParams.isEmpty();
    }
}
