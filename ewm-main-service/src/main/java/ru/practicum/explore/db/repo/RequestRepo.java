package ru.practicum.explore.db.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.request.Request;
import ru.practicum.explore.model.request.RequestStatus;
import ru.practicum.explore.model.user.User;

import java.util.Collection;
import java.util.List;

public interface RequestRepo extends PagingAndSortingRepository<Request, Long> {

    List<Request> findByRequester(User requester);

    List<Request> findByEvent(Event event);

    List<Request> findByIdIn(Collection<Long> ids);

    List<Request> findByEvent_Initiator(User initiator);


    long countByEventAndStatus(Event event, RequestStatus status);

    Request findByRequesterAndEvent(User requester, Event event);
}
