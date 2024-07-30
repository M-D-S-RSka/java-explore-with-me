package ru.practicum.explore.rest.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.model.comments.CommentOutput;
import ru.practicum.explore.service.commentsService.CommentsService;

import java.util.List;

@RestController
@RequestMapping("events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentsRestHandler {
    private final CommentsService commentsService;

    @GetMapping
    public List<CommentOutput> getEventComments(@PathVariable Long eventId) {
        return commentsService.getEventComments(eventId);
    }
}
