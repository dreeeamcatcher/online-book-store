package store.onlinebookstore.mapper;

import java.util.HashSet;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import store.onlinebookstore.dto.book.CreateBookRequestDto;
import store.onlinebookstore.dto.category.CategoryDto;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.model.Category;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(
            target = "categories",
            source = "categories",
            qualifiedByName = "categoriesToCategoriesDto"
    )
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Named("categoriesToCategoriesDto")
    static Set<CategoryDto> categoriesToCategoriesDto(Set<Category> categories) {
        Set<CategoryDto> categoryDtos = new HashSet<>();
        for (Category category : categories) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setName(category.getName());
            categoryDto.setDescription(category.getDescription());
            categoryDtos.add(categoryDto);
        }
        return categoryDtos;
    }

}
