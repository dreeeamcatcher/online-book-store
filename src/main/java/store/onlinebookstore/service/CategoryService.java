package store.onlinebookstore.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import store.onlinebookstore.dto.category.CategoryDto;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto categoryRequestDto);

    CategoryDto updateById(Long id, CategoryDto categoryRequestDto);

    void deleteById(Long id);
}
