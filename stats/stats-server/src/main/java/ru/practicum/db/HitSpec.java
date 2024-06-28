package ru.practicum.db;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.db.model.DbHitData;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class HitSpec {
    public static Specification<DbHitData> searchByUri(List<String> search) {
        search = search.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return Specification.where(like(search));
    }

    private static Specification<DbHitData> like(List<String> searchExpressions) {
        return ((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("uri"));
            for (var searchExpression : searchExpressions) {
                inClause.value(searchExpression);
            }
            return inClause;
        });
    }

}
