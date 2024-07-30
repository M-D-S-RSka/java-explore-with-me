package ru.practicum.explore.service.commentsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.db.repo.CommentRepo;
import ru.practicum.explore.db.repo.EventRepo;
import ru.practicum.explore.db.repo.RequestRepo;
import ru.practicum.explore.db.repo.UserRepo;
import ru.practicum.explore.model.comments.CommentInput;
import ru.practicum.explore.model.comments.CommentOutput;
import ru.practicum.explore.model.exceptions.ConflictException;
import ru.practicum.explore.model.exceptions.NotFoundException;
import ru.practicum.explore.model.request.Request;
import ru.practicum.explore.model.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentMapper commentMapper;
    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final EventRepo eventRepo;
    private final RequestRepo requestRepo;

    public CommentOutput addComment(CommentInput commentInput, Long userId, Long eventId) {
        var commenter = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        var event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("No such event eas found"));
        var participants = requestRepo.findByEventAndStatus(event, RequestStatus.CONFIRMED).stream().map(Request::getRequester).collect(Collectors.toList());
        if (!participants.contains(commenter)) {
            throw new ConflictException("Only confirmed participants can comment event");
        }
        var comment = commentMapper.fromInput(commentInput, commenter, event, LocalDateTime.now(), false);
        return commentMapper.toOutput(commentRepo.save(comment));
    }

    public CommentOutput updateComment(CommentInput commentInput, Long userId, Long commentId) {
        var commenter = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        var comment = commentRepo.findById(commentId).orElseThrow(() -> new NotFoundException("No such comment was found"));
        if (!comment.getCommenter().equals(commenter)) {
            throw new ConflictException("You can update only your comments");
        }
        comment.setComment(commentInput.getComment());
        comment.setUpdated(true);
        comment.setLastUpdate(LocalDateTime.now());
        return commentMapper.toOutput(commentRepo.save(comment));
    }

    public void deleteComment(Long commentId, Long userId) {
        var comment = commentRepo.findById(commentId).orElseThrow(() -> new NotFoundException("No such comment was found"));
        var commenter = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        if (!comment.getCommenter().equals(commenter)) {
            throw new ConflictException("You can delete only your comments");
        }
        commentRepo.deleteById(commentId);
    }

    public List<CommentOutput> getEventComments(Long eventId) {
        var event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("No such event eas found"));
        return commentRepo.findByEvent(event).stream().map(commentMapper::toOutput).collect(Collectors.toList());
    }
}
