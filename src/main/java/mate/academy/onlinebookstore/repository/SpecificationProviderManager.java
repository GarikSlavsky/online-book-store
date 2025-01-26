package mate.academy.onlinebookstore.repository;

import org.springframework.stereotype.Component;

@Component
public interface SpecificationProviderManager<T> {
    SpecificationProvider<T> getSpecificationProvider(String determiner);
}
