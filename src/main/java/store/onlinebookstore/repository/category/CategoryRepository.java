package store.onlinebookstore.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import store.onlinebookstore.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
