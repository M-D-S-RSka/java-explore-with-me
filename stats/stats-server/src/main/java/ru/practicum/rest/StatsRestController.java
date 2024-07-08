package ru.practicum.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsRestController {
    private final StatsService service;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody @Valid HitInput hitInput) {
        service.saveHit(hitInput);
        return ResponseEntity.created(URI.create("/hit")).build();
    }

    @GetMapping("/stats")
    public List<HitOutput> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(defaultValue = "") List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        try {
            return service.getHits(start, end, uris, unique);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Wrong time format");
        }
    }
}
