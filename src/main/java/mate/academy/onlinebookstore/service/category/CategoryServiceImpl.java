package mate.academy.onlinebookstore.service.category;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.category.CategoryResponseDto;
import mate.academy.onlinebookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::intoDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id: " + id + " not found.")
        );
        return categoryMapper.intoDto(category);
    }

    @Override
    public CategoryResponseDto save(CreateCategoryRequestDto categoryDto) {
        Category category = categoryMapper.intoModel(categoryDto);
        return categoryMapper.intoDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto update(Long id, CreateCategoryRequestDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id: " + id + " not found.")
        );

        categoryMapper.updateCategory(categoryDto, category);
        return categoryMapper.intoDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
