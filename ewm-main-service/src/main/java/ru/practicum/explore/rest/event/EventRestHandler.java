package ru.practicum.explore.rest.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.model.event.EventOutput;
import ru.practicum.explore.model.event.EventSort;
import ru.practicum.explore.service.eventService.EventService;
import ru.practicum.explore.utilits.Constants;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventRestHandler {

    private final EventService eventService;

    @GetMapping
    public List<EventOutput> findEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @Valid @DateTimeFormat(pattern = Constants.PATTERN) @RequestParam(required = false) LocalDateTime rangeStartTime,
                                        @Valid @DateTimeFormat(pattern = Constants.PATTERN) @RequestParam(required = false) LocalDateTime rangeEndTime,
                                        @RequestParam(required = false) Boolean onlyAvailable,
                                        @RequestParam(defaultValue = "VIEWS") EventSort sort,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        try {
            if (rangeStartTime != null && rangeEndTime != null && rangeEndTime.isBefore(rangeStartTime)) {
                throw new ValidationException("Start time must be earlie than end time");
            }
            return eventService.searchEvent(text, categories, paid, rangeStartTime, rangeEndTime, onlyAvailable, sort, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Wrong time format");
        }
    }

    @GetMapping("/{eventId}")
    public EventOutput getEventById(@PathVariable long eventId) {
        return eventService.getEventById(eventId);
    }
}
