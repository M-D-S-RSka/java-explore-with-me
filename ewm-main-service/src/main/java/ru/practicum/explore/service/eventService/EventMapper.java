package ru.practicum.explore.service.eventService;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.comments.CommentOutput;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.EventCreateDto;
import ru.practicum.explore.model.event.EventOutput;
import ru.practicum.explore.model.event.EventUpdateDto;
import ru.practicum.explore.model.event.Location;
import ru.practicum.explore.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface EventMapper {
    @Mapping(target = "createdOn", source = "createdOn")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lat", source = "lat")
    @Mapping(target = "lon", source = "lon")
    Event fromInput(EventCreateDto eventInput, Category category, User initiator, LocalDateTime createdOn, Double lat, Double lon);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", source = "views")
    @Mapping(target = "comments", source = "comments")
    EventOutput toOutput(Event event, Location location, long confirmedRequests, int views, List<CommentOutput> comments);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "lat", ignore = true)
    @Mapping(target = "lon", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "annotation", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "paid", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "requestModeration", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "participantLimit", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(EventUpdateDto eventUpdateDto, @MappingTarget Event event);
}
