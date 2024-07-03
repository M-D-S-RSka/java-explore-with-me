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

    public void saveHit(HitInput hitInput, String appName) {
        DbHitData dbHitData = hitsMapper.fromInput(hitInput);
        dbHitData.setApp(appName);
        hitDataDao.saveHit(dbHitData);
    }

    public List<HitOutput> getHits(LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   List<String> uris,
                                   boolean unique,
                                   String appName) {
        if (!endTime.isAfter(startTime)) {
            throw new ValidationException("Invalid time");
        }

        List<HitOutput> res = hitDataDao.getHitOutput(startTime, endTime, uris, unique, appName);

        return res;
    }
}
