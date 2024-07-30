package ru.practicum.explore.db.repo;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.explore.model.compilation.Compilation;

public interface CompilationRepo extends PagingAndSortingRepository<Compilation, Long>, JpaSpecificationExecutor<Compilation> {
}
