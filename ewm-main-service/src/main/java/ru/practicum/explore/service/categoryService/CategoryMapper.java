package ru.practicum.explore.service.categoryService;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.category.CategoryDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface CategoryMapper {
    Category fromDto(CategoryDto categoryDto);

    CategoryDto toDto(Category category);
}
