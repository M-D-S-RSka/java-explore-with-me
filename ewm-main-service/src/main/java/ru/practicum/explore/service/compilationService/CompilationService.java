package ru.practicum.explore.service.compilationService;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.explore.db.CompilationSpec;
import ru.practicum.explore.db.repo.CommentRepo;
import ru.practicum.explore.db.repo.CompilationRepo;
import ru.practicum.explore.db.repo.EventRepo;
import ru.practicum.explore.db.repo.RequestRepo;
import ru.practicum.explore.model.comments.Comment;
import ru.practicum.explore.model.compilation.Compilation;
import ru.practicum.explore.model.compilation.CompilationInputCreate;
import ru.practicum.explore.model.compilation.CompilationInputUpdate;
import ru.practicum.explore.model.compilation.CompilationOutput;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.EventOutput;
import ru.practicum.explore.model.event.Location;
import ru.practicum.explore.model.exceptions.NotFoundException;
import ru.practicum.explore.model.request.RequestStatus;
import ru.practicum.explore.service.commentsService.CommentMapper;
import ru.practicum.explore.service.eventService.EventMapper;
import ru.practicum.model.HitOutput;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PropertySource(value = "application.properties")
public class CompilationService {

    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    private final CommentRepo commentRepo;
    private final EventRepo eventRepo;
    private final RequestRepo requestRepo;
    private final CompilationRepo compilationRepo;
    @Value("${server.url}")
    private String serverUrl;
    private StatsClient statsClient;
    private final Gson gson = new Gson();
    private final CommentMapper commentMapper;
    private final LocalDateTime earliestTime = LocalDateTime.now().minusYears(500);
    private final LocalDateTime latestTime = LocalDateTime.now().plusYears(500);

    @PostConstruct
    private void initStatsClient() {
        statsClient = new StatsClient(serverUrl);
    }


    public CompilationOutput addCompilation(CompilationInputCreate compilationInputCreate) {
        var events = compilationInputCreate.getEvents() == null ? null : eventRepo.findByIdIn(compilationInputCreate.getEvents());
        if (compilationInputCreate.getPinned() == null) compilationInputCreate.setPinned(false);
        var compilation = compilationMapper.fromInput(compilationInputCreate, events);
        return mapToOutput(compilation, events);
    }

    public void deleteCompilation(Long compilationId) {
        var compilation = compilationRepo.findById(compilationId).orElseThrow(() -> new NotFoundException("No such compilation was found"));
        compilationRepo.delete(compilation);
    }

    public CompilationOutput updateCompilation(Long compilationId, CompilationInputUpdate compilationInputUpdate) {
        var compilation = compilationRepo.findById(compilationId).orElseThrow(() -> new NotFoundException("No such compilation was found"));
        var events = compilationInputUpdate.getEvents() == null ? null : eventRepo.findByIdIn(compilationInputUpdate.getEvents());
        var updatedCompilation = compilationMapper.fromInput(compilationInputUpdate, events);
        updatedCompilation.setId(compilationId);
        return mapToOutput(updatedCompilation, events);
    }

    private CompilationOutput mapToOutput(Compilation compilation, List<Event> events) {
        var savedCompilation = compilationRepo.save(compilation);
        var uris = events == null ? new ArrayList<String>() : events.stream().map(it -> String.format("/events/%s", it.getId())).collect(Collectors.toList());
        LocalDateTime earliestTimeLocal;
        if (events == null || events.isEmpty()) {
            earliestTimeLocal = LocalDateTime.now();
        } else {
            earliestTimeLocal = events.stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo).get();
        }
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        var stats = statsClient.getHits(earliestTimeLocal, latestTimeLocal, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        var eventsComments = events == null ? new HashMap<Event, List<Comment>>() : commentRepo.findByEventIn(events).stream().collect(Collectors.groupingBy(Comment::getEvent));
        var eventsOut = savedCompilation.getEvents() == null ? null : savedCompilation.getEvents().stream().map(event -> {
            Location location = new Location(event.getLat(), event.getLon());
            return eventMapper.toOutput(event, location, requestRepo.countByEventAndStatus(event, RequestStatus.CONFIRMED),
                    stats.getOrDefault(String.format("/events/%s", event.getId()), 0),
                    eventsComments.getOrDefault(event, new ArrayList<>()).stream().map(commentMapper::toOutput).collect(Collectors.toList()));
        }).collect(Collectors.toList());
        return compilationMapper.toOutput(savedCompilation, eventsOut);
    }

    public List<CompilationOutput> getCompilations(Boolean pinned, int from, int size) {
        var compilations = findCompilations(pinned, from, size);
        List<String> uris = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        for (var comp : compilations) {
            uris.addAll(comp.getEvents().stream().map(it -> String.format("/events/%s", it.getId())).collect(Collectors.toList()));
            events.addAll(comp.getEvents());
        }
        LocalDateTime earliestTimeLocal;
        if (events == null || events.isEmpty()) {
            earliestTimeLocal = LocalDateTime.now();
        } else {
            earliestTimeLocal = events.stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo).get();
        }
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        var stats = statsClient.getHits(earliestTimeLocal, latestTimeLocal, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        var eventsComments = commentRepo.findByEventIn(events).stream().distinct().collect(Collectors.groupingBy(Comment::getEvent));
        return compilations.stream().map(it -> {
            var eventsOut = mapEventToOutput(it, stats, eventsComments);
            return compilationMapper.toOutput(it, eventsOut);
        }).collect(Collectors.toList());
    }

    public CompilationOutput getCompilation(Long compId) {
        var comp = compilationRepo.findById(compId).orElseThrow(() -> new NotFoundException("No such compilation was found"));
        var uris = comp.getEvents().stream().map(it -> String.format("/events/%s", it.getId())).collect(Collectors.toList());
        LocalDateTime earliestTimeLocal;
        if (comp.getEvents() == null || comp.getEvents().isEmpty()) {
            earliestTimeLocal = LocalDateTime.now();
        } else {
            earliestTimeLocal = comp.getEvents().stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo).get();
        }
        var latestTimeLocal = LocalDateTime.now().plusMinutes(1);
        var stats = statsClient.getHits(earliestTimeLocal, latestTimeLocal, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        var eventsComments = commentRepo.findByEventIn(comp.getEvents()).stream().collect(Collectors.groupingBy(Comment::getEvent));
        var eventsOut = mapEventToOutput(comp, stats, eventsComments);
        return compilationMapper.toOutput(comp, eventsOut);
    }

    private List<EventOutput> mapEventToOutput(Compilation comp, Map<String, Integer> stats, Map<Event, List<Comment>> eventsComments) {
        return comp.getEvents().stream().map((Event event) -> {
            Location location = new Location(event.getLat(), event.getLon());
            return eventMapper.toOutput(event, location, requestRepo.countByEventAndStatus(event, RequestStatus.CONFIRMED), stats.getOrDefault(String.format("/events/%s", event.getId()), 0),
                    eventsComments.getOrDefault(event, new ArrayList<>()).stream().map(commentMapper::toOutput).collect(Collectors.toList()));
        }).collect(Collectors.toList());
    }

    private List<Compilation> findCompilations(Boolean pinned, int from, int size) {
        Specification<Compilation> specification = Specification
                .where(CompilationSpec.isPinned(pinned));

        int page = from % size > 0 ? (from / size) + 1 : from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        return compilationRepo.findAll(specification, pageRequest).toList();
    }
}
