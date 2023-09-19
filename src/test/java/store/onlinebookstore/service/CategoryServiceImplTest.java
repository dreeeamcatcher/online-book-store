package store.onlinebookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.onlinebookstore.dto.category.CategoryDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.CategoryMapper;
import store.onlinebookstore.model.Category;
import store.onlinebookstore.repository.category.CategoryRepository;
import store.onlinebookstore.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Find all categories")
    void findAll_ValidPageable_ReturnsAllCategories() {
        // Given
        Long categoryId = 1L;
        Category fantasyCategory = new Category();
        fantasyCategory.setId(categoryId);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(fantasyCategory);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(fantasyCategoryDto);

        // When
        List<CategoryDto> actualAllCategories = categoryService.findAll(pageable);

        // Then
        assertThat(actualAllCategories).hasSize(1);
        assertEquals(fantasyCategoryDto, actualAllCategories.get(0));

        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category by id")
    void getById_ValidCategoryId_ReturnsCategoryDto() {
        // Given
        Long categoryId = 1L;
        Category fantasyCategory = new Category();
        fantasyCategory.setId(categoryId);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(fantasyCategory));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(fantasyCategoryDto);

        // When
        CategoryDto actual = categoryService.getById(categoryId);

        // Then
        assertEquals(fantasyCategoryDto, actual);
        verify(categoryRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category with not valid id")
    void getById_NotValidCategoryId_ThrowsException() {
        // Given
        Long categoryId = 1L;
        Category fantasyCategory = new Category();
        fantasyCategory.setId(categoryId);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        Long nonExistingId = 100L;

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(nonExistingId));

        // Then
        String expected = "Can't find category by id = " + nonExistingId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Update category")
    void updateById_ValidCategoryId_ReturnsUpdatedCategoryDto() {
        // Given
        CategoryDto request = new CategoryDto();
        request.setName("Updated Fantasy");
        request.setDescription("Updated Fantasy");

        Category categoryFromRequest = new Category();
        categoryFromRequest.setName(request.getName());
        categoryFromRequest.setDescription(request.getDescription());

        Long categoryId = 1L;
        Category categoryFromDb = new Category();
        categoryFromDb.setId(categoryId);
        categoryFromDb.setName("Fantasy");
        categoryFromDb.setDescription("Fantasy");

        CategoryDto dtoFromDb = new CategoryDto();
        dtoFromDb.setName(categoryFromDb.getName());
        dtoFromDb.setDescription(categoryFromDb.getDescription());
        dtoFromDb.setId(categoryFromDb.getId());

        CategoryDto dtoFromRequest = new CategoryDto();
        dtoFromRequest.setId(categoryId);
        dtoFromRequest.setName(categoryFromRequest.getName());
        dtoFromRequest.setDescription(categoryFromRequest.getDescription());

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoryFromDb));
        when(categoryMapper.toDto(categoryFromDb)).thenReturn(dtoFromDb);
        when(categoryMapper.toModel(request)).thenReturn(categoryFromRequest);
        when(categoryRepository.save(categoryFromRequest)).thenReturn(categoryFromRequest);
        when(categoryMapper.toDto(categoryFromRequest)).thenReturn(dtoFromRequest);

        // When
        CategoryDto updatedDto = categoryService.updateById(categoryId, request);

        // Then
        assertEquals(dtoFromRequest, updatedDto);
        verify(categoryRepository, times(1)).findById(anyLong());
        verify(categoryRepository, times(1)).save(categoryFromRequest);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }
}
