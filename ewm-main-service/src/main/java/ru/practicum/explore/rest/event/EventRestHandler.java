package ru.practicum.explore.rest.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.model.event.EventOutput;
import ru.practicum.explore.model.event.EventSort;
import ru.practicum.explore.service.eventService.EventService;
import ru.practicum.explore.utilits.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventRestHandler {

    private final EventService eventService;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constants.PATTERN);

    @GetMapping
    public List<EventOutput> findEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(required = false) Boolean onlyAvailable,
                                        @RequestParam(defaultValue = "VIEWS") EventSort sort,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        try {
            var rangeStartTime = rangeStart == null ? null : LocalDateTime.parse(rangeStart, dtf);
            var rangeEndTime = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, dtf);
            if (rangeStartTime != null && rangeEndTime != null && rangeEndTime.isBefore(rangeStartTime))
                throw new ValidationException("Start time must be earlie than end time");
            return eventService.searchEvent(text, categories, paid, rangeStartTime, rangeEndTime, onlyAvailable, sort,
                    from, size, request.getRemoteAddr(), request.getServletPath());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Wrong time format");
        }
    }

    @GetMapping("/{eventId}")
    public EventOutput getEventById(@PathVariable long eventId, HttpServletRequest request) {
        return eventService.getEventById(eventId, request.getRemoteAddr(), request.getServletPath());
    }
}
