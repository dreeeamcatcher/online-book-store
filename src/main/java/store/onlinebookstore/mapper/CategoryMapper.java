package store.onlinebookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.category.CategoryDto;
import store.onlinebookstore.model.Category;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    @Mapping(target = "deleted", ignore = true)
    Category toModel(CategoryDto categoryRequestDto);
}
