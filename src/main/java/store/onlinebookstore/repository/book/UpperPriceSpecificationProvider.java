package store.onlinebookstore.repository.book;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.repository.SpecificationProvider;

@Component
public class UpperPriceSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "upperPrice";
    }

    public Specification<Book> getSpecification(String[] params) {
        BigDecimal upperPrice = BigDecimal.valueOf(Long.parseLong(params[0]));

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("price"), upperPrice);
    }
}
