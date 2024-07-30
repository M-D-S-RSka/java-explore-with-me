package ru.practicum.explore.db.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.explore.model.comments.Comment;
import ru.practicum.explore.model.event.Event;

import java.util.Collection;
import java.util.List;

public interface CommentRepo extends PagingAndSortingRepository<Comment, Long> {

    List<Comment> findByEvent(Event event);

    List<Comment> findByEventIn(Collection<Event> events);
}
