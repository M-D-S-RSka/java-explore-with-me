package ru.practicum.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsRestController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public String saveHit(@RequestBody @Valid HitInput hitInput) {
        service.saveHit(hitInput);
        return "Информация сохранена";
    }

    @GetMapping("/stats")
    public List<HitOutput> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(defaultValue = "") List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        return service.getHits(start, end, uris, unique);
    }
}
