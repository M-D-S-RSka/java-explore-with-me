package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.db.HitDataDao;
import ru.practicum.db.model.DbHitData;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {


    private final HitDataDao dao;
    private final HitsMapper hitsMapper;

    public void saveHit(HitInput hitInput) {
        dao.saveHit(hitsMapper.fromInput(hitInput));
    }

    public List<HitOutput> getHits(LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   List<String> uris,
                                   boolean unique) {
        if (!endTime.isAfter(startTime)) {
            throw new ValidationException("Invalid time");
        }

        List<HitOutput> res;

        if (!uris.isEmpty()) {
            res = dao.searchByUri(uris).stream()
                    .collect(Collectors.groupingBy(DbHitData::getUri,
                            Collectors.mapping(DbHitData::getIp, Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> {
                        HitOutput item = new HitOutput();
                        item.setApp("ewm-main-service");
                        item.setUri(entry.getKey());
                        item.setHits((long) (unique ? entry.getValue().stream().distinct().count() : entry.getValue()
                                .size()));
                        return item;
                    })
                    .sorted(Comparator.comparingLong(HitOutput::getHits).reversed())
                    .collect(Collectors.toList());
        } else {
            res = dao.findAllHits().stream()
                    .collect(Collectors.groupingBy(DbHitData::getUri,
                            Collectors.mapping(DbHitData::getIp, Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> {
                        HitOutput item = new HitOutput();
                        item.setApp("ewm-main-service");
                        item.setUri(entry.getKey());
                        item.setHits((long) (unique ? entry.getValue().stream().distinct().count() : entry.getValue()
                                .size()));
                        return item;
                    })
                    .sorted(Comparator.comparingLong(HitOutput::getHits).reversed())
                    .collect(Collectors.toList());
        }

        return res;
    }
}
