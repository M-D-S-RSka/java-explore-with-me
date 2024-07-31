package ru.practicum.explore.service.commentsService;


import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.model.comments.Comment;
import ru.practicum.explore.model.comments.CommentInput;
import ru.practicum.explore.model.comments.CommentOutput;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.user.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commenter", source = "commenter")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "lastUpdate", source = "lastUpdate")
    @Mapping(target = "updated", source = "updated")
    Comment fromInput(CommentInput commentInput, User commenter, Event event, LocalDateTime lastUpdate, boolean updated);

    @Mapping(target = "userName", source = "comment.commenter.name")
    CommentOutput toOutput(Comment comment);

}
