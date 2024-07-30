package ru.practicum.explore.service.compilationService;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.model.compilation.Compilation;
import ru.practicum.explore.model.compilation.CompilationInputCreate;
import ru.practicum.explore.model.compilation.CompilationInputUpdate;
import ru.practicum.explore.model.compilation.CompilationOutput;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.EventOutput;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface CompilationMapper {
    @Mapping(target = "events", source = "events")
    Compilation fromInput(CompilationInputCreate compilationInputCreate, List<Event> events);

    @Mapping(target = "events", source = "events")
    Compilation fromInput(CompilationInputUpdate compilationInputCreate, List<Event> events);

    @Mapping(target = "events", source = "events")
    @Mapping(target = "id", source = "compilation.id")
    CompilationOutput toOutput(Compilation compilation, List<EventOutput> events);
}
