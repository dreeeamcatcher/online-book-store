package store.onlinebookstore.repository.book;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.repository.SpecificationProvider;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    public Specification<Book> getSpecification(String[] params) {
        String title = params[0];
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }
}
