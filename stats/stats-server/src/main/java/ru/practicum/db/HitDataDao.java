package ru.practicum.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.db.model.DbHitData;
import ru.practicum.model.HitOutput;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HitDataDao {

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

    public List<HitOutput> getHitOutput(LocalDateTime startTime,
                                        LocalDateTime endTime,
                                        List<String> uris,
                                        boolean unique,
                                        String appName) {
        Specification<DbHitData> specification = Specification
                .where(HitSpec.getHitOutput(startTime, endTime, uris, unique, appName));
        return List.of();
    }
}
