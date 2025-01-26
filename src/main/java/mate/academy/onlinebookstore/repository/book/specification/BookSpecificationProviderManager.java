package mate.academy.onlinebookstore.repository.book.specification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationProvider;
import mate.academy.onlinebookstore.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String determiner) {
        return specificationProviders.stream()
                .filter(p -> p.getDeterminer().equals(determiner))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                                "Could not find specification provider for determiner: "
                                        + determiner));
    }
}
