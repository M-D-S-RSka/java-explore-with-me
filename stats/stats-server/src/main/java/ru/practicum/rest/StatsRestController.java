package ru.practicum.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
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
    public List<HitOutput> getHits(@RequestParam @Valid @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam @Valid @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(defaultValue = "") List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        return service.getHits(start, end, uris, unique);
    }
}
