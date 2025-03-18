package mate.academy.onlinebookstore.util;

import java.util.ArrayList;
import java.util.List;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.dto.category.CreateCategoryRequestDto;

public class CategoryControllerUtilTest {
    public List<CategoryResponseDto> getCategoryResponseDtoList() {
        final CategoryResponseDto responseDto1 = initializeCategoryResponseDto();
        final CategoryResponseDto responseDto2 = new CategoryResponseDto();
        responseDto2.setId(2L);
        responseDto2.setName("Fantasy");
        responseDto2.setDescription("Stories that involve magic or other supernatural elements.");

        List<CategoryResponseDto> expected = new ArrayList<>();
        expected.add(responseDto1);
        expected.add(responseDto2);
        return expected;
    }

    public CategoryResponseDto initializeCategoryResponseDto() {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(1L);
        dto.setName("Literary fiction");
        dto.setDescription("A comprehensive guide to best practices in literary fiction.");
        return dto;
    }

    public CreateCategoryRequestDto initializeCreateCategoryRequestDto() {
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
        dto.setName("Literary fiction");
        dto.setDescription("A comprehensive guide to best practices in literary fiction.");
        return dto;
    }
}
