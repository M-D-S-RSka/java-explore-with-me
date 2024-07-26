package ru.practicum.explore.rest.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.event.EventCreateDto;
import ru.practicum.explore.model.event.EventOutput;
import ru.practicum.explore.model.event.EventUpdateUserDto;
import ru.practicum.explore.model.request.ConfirmeRequestsInput;
import ru.practicum.explore.model.request.GroupedRequestsOutput;
import ru.practicum.explore.model.request.RequestOutput;
import ru.practicum.explore.service.eventService.EventService;
import ru.practicum.explore.service.requestService.RequestsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventRestHandler {

    private final EventService eventService;
    private final RequestsService requestsService;

    @GetMapping
    public List<EventOutput> getUserMadeEvents(@PathVariable long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return eventService.getUsersEvents(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<EventOutput> createEvent(@PathVariable long userId, @Valid @RequestBody EventCreateDto eventCreateDto) {
        return new ResponseEntity<>(eventService.createEvent(userId, eventCreateDto), HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public EventOutput getUserEvent(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventByIdAndUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventOutput updateEvent(@PathVariable long userId, @PathVariable long eventId, @Valid @RequestBody EventUpdateUserDto eventUpdateDto) {
        return eventService.updateEvent(userId, eventId, eventUpdateDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestOutput> getUserEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        return requestsService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public GroupedRequestsOutput confirmRequest(@PathVariable Long eventId,
                                                @Valid @RequestBody ConfirmeRequestsInput requestsInput) {
        return requestsService.updateRequestStatus(eventId, requestsInput);
    }
}
