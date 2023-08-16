package store.onlinebookstore.repository.book;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.repository.SpecificationProvider;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "authors";
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get("author").in(Arrays.stream(params).toArray());
    }
}
