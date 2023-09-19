package store.onlinebookstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import store.onlinebookstore.model.Category;
import store.onlinebookstore.repository.category.CategoryRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Sql(scripts = "classpath:database/categories/add-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/remove-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get categories by ids")
    void getCategoriesByIdIn_ValidCategoryIds_ReturnsListOfCategories() {
        Set<Category> oneCategory = categoryRepository.getCategoriesByIdIn(List.of(1L));
        Set<Category> twoCategories = categoryRepository.getCategoriesByIdIn(List.of(2L, 3L));

        assertThat(oneCategory).hasSize(1);
        assertThat(twoCategories).hasSize(2);
    }
}
