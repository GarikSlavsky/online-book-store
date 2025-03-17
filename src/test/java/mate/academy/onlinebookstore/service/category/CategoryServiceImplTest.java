package mate.academy.onlinebookstore.service.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
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

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Verify save method works properly.")
    void save_ValidRequestDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto categoryDto = new CreateCategoryRequestDto();
        categoryDto.setName("Category Name");

        Category category = new Category();
        category.setName(categoryDto.getName());
        CategoryResponseDto expected = initializeCategoryResponseDto(category);

        when(categoryMapper.intoModel(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.intoDto(category)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.save(categoryDto);
        assertThat(actual).isEqualTo(expected);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Get a list of categories.")
    void findAll_ReturnsCategoryDtoList() {
        Category category1 = new Category();
        category1.setName("Category Name");
        Category category2 = new Category();
        category2.setName("Another Category Name");

        CategoryResponseDto dto1 = initializeCategoryResponseDto(category1);
        CategoryResponseDto dto2 = new CategoryResponseDto();
        dto2.setId(2L);
        dto2.setName(category2.getName());

        List<CategoryResponseDto> expected = List.of(dto1, dto2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category1, category2));

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.intoDto(category1)).thenReturn(dto1);
        when(categoryMapper.intoDto(category2)).thenReturn(dto2);

        List<CategoryResponseDto> actual = categoryService.findAll(pageable);
        assertThat(actual).isEqualTo(expected);
        assertThat(actual).hasSize(2);
    }

    @Test
    @DisplayName("Get category by its ID.")
    void getById_ReturnsCategoryDto() {
        Category category = new Category();
        category.setName("Category Name");
        CategoryResponseDto expected = initializeCategoryResponseDto(category);

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.intoDto(category)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.getById(100L);
        assertThat(actual).isEqualTo(expected);
        verify(categoryRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Get category by ID - Nonexistent ID throws exception.")
    void getById_NonExistentId_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(100L));
        assertThat(thrown.getMessage()).isEqualTo("Category with id: 100 not found.");
        verify(categoryRepository, times(1)).findById(100L);
    }

    private static CategoryResponseDto initializeCategoryResponseDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(1L);
        dto.setName(category.getName());
        return dto;
    }
}
