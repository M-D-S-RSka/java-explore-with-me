package ru.practicum.explore.service.requestService;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.model.request.Request;
import ru.practicum.explore.model.request.RequestOutput;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface RequestMapper {
    @Mapping(target = "requester", source = "request.requester.id")
    @Mapping(target = "event", source = "request.event.id")
    RequestOutput toOutput(Request request);
}
