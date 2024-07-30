package ru.practicum.explore.db.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.EventState;
import ru.practicum.explore.model.user.User;

import java.util.Collection;
import java.util.List;

public interface EventRepo extends PagingAndSortingRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByInitiator(User initiator, Pageable pageable);

    Event findByInitiatorAndId(User initiator, Long id);

    List<Event> findByIdIn(Collection<Long> ids);

    Event findByIdAndState(Long id, EventState state);

}
