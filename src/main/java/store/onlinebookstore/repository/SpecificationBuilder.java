package store.onlinebookstore.repository;

import org.springframework.data.jpa.domain.Specification;
import store.onlinebookstore.dto.book.BookSearchParameters;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParameters searchParameters);
}
