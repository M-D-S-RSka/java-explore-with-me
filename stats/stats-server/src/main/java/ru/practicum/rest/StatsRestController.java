package ru.practicum.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatsRestController {
    private final StatsService service;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody @Valid HitInput hitInput) {
        service.saveHit(hitInput);
        return ResponseEntity.created(URI.create("/hit")).build();
    }

    @GetMapping("/stats")
    public List<HitOutput> getHits(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(defaultValue = "") List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {

        try {
            var startTime = LocalDateTime.parse(start, dtf);
            var endTime = LocalDateTime.parse(end, dtf);
            return service.getHits(startTime, endTime, uris, unique);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Wrong time format");
        }
    }
}
