package ru.practicum.explore.service.categoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.db.repo.CategoryRepo;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.exceptions.ConflictException;
import ru.practicum.explore.model.exceptions.NotFoundException;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepo categoryRepo;

    public CategoryDto createCategory(CategoryDto categoryDto) {
        try {
            return categoryMapper.toDto(categoryRepo.save(categoryMapper.fromDto(categoryDto)));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category name is already used");
        }
    }

    public void deleteCategory(long id) {
        try {
            categoryRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category is using");
        }

    }

    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        var oldCat = categoryRepo.findById(catId).orElse(null);
        if (oldCat != null) {
            try {
                var cat = categoryRepo.save(categoryMapper.fromDto(categoryDto));
                return categoryMapper.toDto(cat);
            } catch (DataIntegrityViolationException e) {
                if (oldCat.getName().equals(categoryDto.getName())) {
                    return categoryDto;
                }
                throw new ConflictException("Category name is already used");
            }
        } else {
            throw new ValidationException("No such category was found");
        }
    }

    public List<CategoryDto> getCategories(int from, int size) {
        return getAllCategoriesPage(from, size).stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(long id) {
        var cat = categoryRepo.findById(id).orElseThrow(() -> new NotFoundException("No such category was found"));
        if (cat == null) throw new NotFoundException("No such category was found");
        return categoryMapper.toDto(cat);
    }

    private List<Category> getAllCategoriesPage(int from, int size) {
        int page = from % size > 0 ? (from / size) + 1 : from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryRepo.findAll(pageRequest).toList();
    }
}
