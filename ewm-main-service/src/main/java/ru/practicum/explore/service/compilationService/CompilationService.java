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
import ru.practicum.explore.db.repo.CompilationRepo;
import ru.practicum.explore.db.repo.EventRepo;
import ru.practicum.explore.db.repo.RequestRepo;
import ru.practicum.explore.model.compilation.Compilation;
import ru.practicum.explore.model.compilation.CompilationInputCreate;
import ru.practicum.explore.model.compilation.CompilationInputUpdate;
import ru.practicum.explore.model.compilation.CompilationOutput;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.EventOutput;
import ru.practicum.explore.model.event.Location;
import ru.practicum.explore.model.exceptions.NotFoundException;
import ru.practicum.explore.model.request.RequestStatus;
import ru.practicum.explore.service.eventService.EventMapper;
import ru.practicum.model.HitOutput;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PropertySource(value = "application.properties")
public class CompilationService {

    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    private final EventRepo eventRepo;
    private final RequestRepo requestRepo;
    private final CompilationRepo compilationRepo;
    @Value("${server.url}")
    private String serverUrl;
    private StatsClient statsClient;
    private final Gson gson = new Gson();
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
        var stats = statsClient.getHits(earliestTime, latestTime, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        var eventsOut = savedCompilation.getEvents() == null ? null : savedCompilation.getEvents().stream().map(event -> {
            Location location = new Location(event.getLat(), event.getLon());
            return eventMapper.toOutput(event, location, requestRepo.countByEventAndStatus(event, RequestStatus.CONFIRMED), stats.getOrDefault(String.format("/events/%s", event.getId()), 0));
        }).collect(Collectors.toList());
        return compilationMapper.toOutput(savedCompilation, eventsOut);
    }

    public List<CompilationOutput> getCompilations(Boolean pinned, int from, int size) {
        var compilations = findCompilations(pinned, from, size);
        List<String> uris = new ArrayList<>();
        for (var comp : compilations) {
            uris.addAll(comp.getEvents().stream().map(it -> String.format("/events/%s", it.getId())).collect(Collectors.toList()));
        }
        var stats = statsClient.getHits(earliestTime, latestTime, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        return compilations.stream().map(it -> {
            var eventsOut = mapEventToOutput(it, stats);
            return compilationMapper.toOutput(it, eventsOut);
        }).collect(Collectors.toList());
    }

    public CompilationOutput getCompilation(Long compId) {
        var comp = compilationRepo.findById(compId).orElseThrow(() -> new NotFoundException("No such compilation was found"));
        var uris = comp.getEvents().stream().map(it -> String.format("/events/%s", it.getId())).collect(Collectors.toList());
        var stats = statsClient.getHits(earliestTime, latestTime, uris, true).stream()
                .collect(Collectors.toMap((HitOutput it) -> it.getUri().substring(it.getUri().lastIndexOf("/") + 1), (HitOutput::getHits)));
        var eventsOut = mapEventToOutput(comp, stats);
        return compilationMapper.toOutput(comp, eventsOut);
    }

    private List<EventOutput> mapEventToOutput(Compilation comp, Map<String, Integer> stats) {
        return comp.getEvents().stream().map((Event event) -> {
            Location location =new Location(event.getLat(), event.getLon());
            return eventMapper.toOutput(event, location, requestRepo.countByEventAndStatus(event, RequestStatus.CONFIRMED), stats.getOrDefault(String.format("/events/%s", event.getId()), 0));
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
