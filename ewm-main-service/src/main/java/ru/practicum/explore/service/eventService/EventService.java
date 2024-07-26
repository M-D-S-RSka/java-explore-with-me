package ru.practicum.explore.service.eventService;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.explore.db.EventSpec;
import ru.practicum.explore.db.repo.CategoryRepo;
import ru.practicum.explore.db.repo.EventRepo;
import ru.practicum.explore.db.repo.RequestRepo;
import ru.practicum.explore.db.repo.UserRepo;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.event.*;
import ru.practicum.explore.model.exceptions.ConflictException;
import ru.practicum.explore.model.exceptions.NotFoundException;
import ru.practicum.explore.model.request.RequestDto;
import ru.practicum.explore.model.request.RequestStatus;
import ru.practicum.explore.model.user.User;
import ru.practicum.model.HitOutput;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PropertySource(value = "application.properties")
public class EventService {
    private final EventMapper eventMapper;
    private final Gson gson = new Gson();
    @Value("${server.url}")
    private String serverUrl;
    private StatsClient statsClient;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;
    private final RequestRepo requestRepo;

    @PostConstruct
    private void initStatsClient() {
        statsClient = new StatsClient(serverUrl);
    }


    public EventOutput createEvent(Long userId, EventCreateDto eventInput) {
        if (eventInput.getRequestModeration() == null) eventInput.setRequestModeration(true);
        var category = categoryRepo.findById(eventInput.getCategory()).orElse(null);
        var initiator = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("No such initiator was found"));
        Double lat = eventInput.getLocation().getLat();
        Double lon = eventInput.getLocation().getLon();
        var event = eventMapper.fromInput(eventInput, category, initiator, LocalDateTime.now(), lat, lon);
        event.setState(EventState.PENDING);
        var savedEvent = eventRepo.save(event);
        Location location = new Location(event.getLat(), event.getLon());
        return eventMapper.toOutput(savedEvent, location, 0, 0);
    }

    public List<EventOutput> getUsersEvents(long userId, int from, int size) {
        var initiator = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("No such initiator was found"));
        int page = from % size > 0 ? (from / size) + 1 : from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        var events = eventRepo.findByInitiator(initiator, pageRequest);
        if (events.isEmpty()) {
            return List.of();
        }
        var earliestTimeLocal = events.stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo);
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        var uris = events.stream().map(it -> String.format("/events/%s", it.getId())).collect(Collectors.toList());
        var stats = statsClient.getHits(earliestTimeLocal.get(), latestTimeLocal, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        var countByEventIdMap = requestRepo.getCountByEventIdListAndStatus(events, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(RequestDto::getEventId, RequestDto::getCount));
        return events.stream().map(event -> {
            Location location = new Location(event.getLat(), event.getLon());
            return eventMapper.toOutput(event, location, countByEventIdMap.getOrDefault(event.getId(), 0L), stats.getOrDefault(String.format("/events/%s", event.getId()), 0));
        }).collect(Collectors.toList());
    }

    public EventOutput getEventByIdAndUser(long userId, long eventId) {
        var initiator = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("No such initiator was found"));
        var event = eventRepo.findByInitiatorAndId(initiator, eventId);
        var earliestTimeLocal = event.getCreatedOn();
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        var stats = statsClient.getHits(earliestTimeLocal, latestTimeLocal, List.of(String.format("/events/%s", eventId)), true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        Location location = new Location(event.getLat(), event.getLon());
        return eventMapper.toOutput(event, location, requestRepo.countByEventAndStatus(event, RequestStatus.CONFIRMED), stats.getOrDefault(String.format("/events/%s", eventId), 0));
    }

    public EventOutput updateEvent(long userId, long eventId, EventUpdateUserDto eventInput) {
        var initiator = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("No such initiator was found"));
        var event = eventRepo.findByInitiatorAndId(initiator, eventId);
        if (event == null) {
            throw new NotFoundException("No such event was found");
        }
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Event already published");
        }
        eventMapper.update(eventInput, event);
        if (eventInput.getEventDate() != null) {
            event.setEventDate(eventInput.getEventDate());
        }
        if (eventInput.getStateAction() != null) {
            switch (eventInput.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
            }
        }
        var savedEvent = eventRepo.save(event);
        Location location = new Location(savedEvent.getLat(), event.getLon());
        return eventMapper.toOutput(savedEvent, location, 0, 0);
    }

    public EventOutput updateEventAdmin(long eventId, EventUpdateAdminDto eventInput) {
        var event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("No such event was found"));
        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Event not ready to approve");
        }
        eventMapper.update(eventInput, event);
        if (eventInput.getEventDate() != null) {
            event.setEventDate(eventInput.getEventDate());
        }
        if (eventInput.getStateAction() != null) {
            switch (eventInput.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        var savedEvent = eventRepo.save(event);
        Location location = new Location(savedEvent.getLat(), event.getLon());
        return eventMapper.toOutput(savedEvent, location, 0, 0);
    }

    public List<EventOutput> searchEvent(String text, List<Long> categoriesIds, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Boolean onlyAvailable, EventSort sort, int from, int size) {
        var categories = categoriesIds == null ? null : categoryRepo.findByIdIn(categoriesIds);
        var events = searchManyFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);
        if (events.isEmpty()) {
            return List.of();
        }
        var earliestTimeLocal = events.stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo);
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        var uris = events.stream().map(it -> String.format("/events/%s", it.getId())).collect(Collectors.toList());
        statsClient.sendStatsHit("some ip", "explore-with-me-main-service", "/events");
        var stats = statsClient.getHits(earliestTimeLocal.get(), latestTimeLocal, uris, true).stream().collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));

        var countByEventIdMap = requestRepo.getCountByEventIdListAndStatus(events, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(RequestDto::getEventId, RequestDto::getCount));
        var result = events.stream().map(event -> {
            Location location = new Location(event.getLat(), event.getLon());
            return eventMapper.toOutput(event, location, countByEventIdMap.getOrDefault(event.getId(), 0L), (stats.getOrDefault(String.valueOf(event.getId()), 0)));
        }).collect(Collectors.toList());
        switch (sort) {
            case EVENT_DATE:
                result = result.stream().sorted(Comparator.comparing(EventOutput::getEventDate)).collect(Collectors.toList());
                break;
            case VIEWS:
                result = result.stream().sorted(Comparator.comparing(EventOutput::getViews)).collect(Collectors.toList());
                break;
        }
        return result;
    }

    public Set<EventOutput> searchEventAdmin(List<Long> usersIds,
                                             List<EventState> states,
                                             List<Long> categoriesIds,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             int from,
                                             int size) {
        var categories = categoriesIds == null ? null : categoryRepo.findByIdIn(categoriesIds);
        var users = usersIds == null ? null : userRepo.findByIdIn(usersIds);
        List<String> uris = new ArrayList<>();

        var events = searchManyFiltersAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        if (events.isEmpty()) {
            return Set.of();
        }
        var earliestTimeLocal = events.stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo);
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        events.forEach(event -> uris.add(String.format("/events/%s", event.getId())));
        var stats = statsClient.getHits(earliestTimeLocal.get(), latestTimeLocal, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));

        var rawRes = requestRepo.getCountByEventIdListAndStatus(events, RequestStatus.CONFIRMED);
        var countByEventIdMap = rawRes.stream()
                .collect(Collectors.toMap(RequestDto::getEventId, RequestDto::getCount));
        return events.stream().map(event -> {
            Location location = new Location(event.getLat(), event.getLon());
            return eventMapper.toOutput(event, location, countByEventIdMap.getOrDefault(event.getId(), 0L), (stats.getOrDefault(String.valueOf(event.getId()), 0)));
        }).collect(Collectors.toSet());
    }

    public EventOutput getEventById(long eventId) {
        var event = eventRepo.findByIdAndState(eventId, EventState.PUBLISHED);
        if (event == null) {
            throw new NotFoundException("No such event was found");
        }
        var earliestTimeLocal = event.getCreatedOn();
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        statsClient.sendStatsHit("some ip", "explore-with-me-main-service", String.format("/events/%s", eventId));
        Location location = new Location(event.getLat(), event.getLon());
        var stats = statsClient.getHits(earliestTimeLocal, latestTimeLocal, List.of(String.format("/events/%s", eventId)), true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        return eventMapper.toOutput(event, location, requestRepo.countByEventAndStatus(event, RequestStatus.CONFIRMED), (stats.getOrDefault(String.valueOf(eventId), 0)));
    }


    private List<Event> searchManyFilters(String text, List<Category> categories, Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                          Boolean onlyAvailable, int from, int size) {
        Specification<Event> specification = Specification
                .where(EventSpec.searchByText(text))
                .and(EventSpec.categoryIn(categories))
                .and(EventSpec.isPaid(paid))
                .and(EventSpec.rangeStart(rangeStart))
                .and(EventSpec.rangeEnd(rangeEnd))
                .and(EventSpec.isAvailable(onlyAvailable));

        int page = from % size > 0 ? (from / size) + 1 : from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        return eventRepo.findAll(specification, pageRequest).toList();
    }

    private List<Event> searchManyFiltersAdmin(List<User> users, List<EventState> states, List<Category> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Specification<Event> specification = Specification
                .where(EventSpec.categoryIn(categories))
                .and(EventSpec.userIn(users))
                .and(EventSpec.stateIn(states))
                .and(EventSpec.rangeStart(rangeStart))
                .and(EventSpec.rangeEnd(rangeEnd));

        int page = from % size > 0 ? (from / size) + 1 : from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        return eventRepo.findAll(specification, pageRequest).toList();
    }
}
