package ru.practicum.explore.db.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.explore.model.category.Category;

import java.util.Collection;
import java.util.List;

public interface CategoryRepo extends PagingAndSortingRepository<Category, Long> {
    List<Category> findByIdIn(Collection<Long> ids);
}
