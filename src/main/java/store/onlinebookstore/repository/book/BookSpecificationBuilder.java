package store.onlinebookstore.repository.book;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import store.onlinebookstore.dto.BookSearchParameters;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.repository.SpecificationBuilder;
import store.onlinebookstore.repository.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Autowired
    public BookSpecificationBuilder(
            SpecificationProviderManager<Book> bookSpecificationProviderManager) {
        this.bookSpecificationProviderManager = bookSpecificationProviderManager;
    }

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("authors")
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.title() != null) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("title")
                    .getSpecification(searchParameters.title()));
        }
        if (searchParameters.lowerPrice() != null) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("lowerPrice")
                    .getSpecification(searchParameters.lowerPrice()));
        }
        if (searchParameters.upperPrice() != null) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("upperPrice")
                    .getSpecification(searchParameters.upperPrice()));
        }

        return spec;
    }
}
