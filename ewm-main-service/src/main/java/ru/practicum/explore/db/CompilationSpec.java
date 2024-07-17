package ru.practicum.explore.db;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.explore.model.compilation.Compilation;

import java.util.Objects;

public class CompilationSpec {

    public static Specification<Compilation> isPinned(Boolean pinned) {
        if (Objects.isNull(pinned)) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pinned"), pinned));
    }
}
