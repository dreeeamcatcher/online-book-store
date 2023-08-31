package store.onlinebookstore.repository.category;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import store.onlinebookstore.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Set<Category> getCategoriesByIdIn(List<Long> id);
}
