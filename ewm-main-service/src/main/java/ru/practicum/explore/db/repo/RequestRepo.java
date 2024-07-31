package ru.practicum.explore.db.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.request.Request;
import ru.practicum.explore.model.request.RequestDto;
import ru.practicum.explore.model.request.RequestStatus;
import ru.practicum.explore.model.user.User;

import java.util.Collection;
import java.util.List;

public interface RequestRepo extends PagingAndSortingRepository<Request, Long> {

    List<Request> findByRequester(User requester);

    List<Request> findByEvent(Event event);

    List<Request> findByIdIn(Collection<Long> ids);

    List<Request> findByEvent_Initiator(User initiator);

    @Query("SELECT new ru.practicum.explore.model.request.RequestDto(r.event.id, count(r.requester)) FROM Request r WHERE r.event in ?1 and r.status = ?2 GROUP BY r.id")
    List<RequestDto> getCountByEventIdListAndStatus(Collection<Event> events, RequestStatus status);

    long countByEventAndStatus(Event event, RequestStatus status);

    Request findByRequesterAndEvent(User requester, Event event);

    List<Request> findByEventAndStatus(Event event, RequestStatus status);
}