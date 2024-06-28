package ru.practicum.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.db.model.DbHitData;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Dao {

    private final StatsRepo statsRepo;

    public List<DbHitData> searchByUri(List<String> uri) {
        Specification<DbHitData> specification = Specification
                .where(HitSpec.searchByUri(uri));

        return statsRepo.findAll(specification);
    }

    public List<DbHitData> findAllHits() {
        return statsRepo.findAll();
    }

    public void saveHit(DbHitData hitData) {
        statsRepo.save(hitData);
    }
}
