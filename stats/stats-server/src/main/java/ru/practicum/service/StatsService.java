package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.db.HitDataDao;
import ru.practicum.db.model.DbHitData;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {


    private final HitDataDao hitDataDao;
    private final HitsMapper hitsMapper;

    public void saveHit(HitInput hitInput) {
        hitDataDao.saveHit(hitsMapper.fromInput(hitInput));
    }

    public List<HitOutput> getHits(LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   List<String> uris,
                                   boolean unique) {
        if (!endTime.isAfter(startTime)) {
            throw new ValidationException("Invalid time");
        }
        Map<String, List<DbHitData>> rawHits;
        if (!uris.isEmpty()) {
            rawHits = hitDataDao.searchByUri(uris).stream().collect(Collectors.groupingBy(DbHitData::getUri));
        } else {
            rawHits = hitDataDao.findAllHits().stream().collect(Collectors.groupingBy(DbHitData::getUri));
        }

        var res = new ArrayList<HitOutput>();

        for (var pair : rawHits.entrySet()) {
            var hitsStream = pair.getValue().stream().map(DbHitData::getIp);
            if (unique) hitsStream = hitsStream.distinct();
            var hits = hitsStream.collect(Collectors.toList());
            var item = new HitOutput();
            DbHitData dbHitData = new DbHitData();
            item.setApp(dbHitData.getApp());
            item.setUri(pair.getKey());
            item.setHits((long) hits.size());
            res.add(item);
        }

        return res.stream().sorted(Comparator.comparingLong(hitOutput -> -hitOutput.getHits())).collect(Collectors.toList());
    }
}
