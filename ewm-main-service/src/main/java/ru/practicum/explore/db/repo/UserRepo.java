package ru.practicum.explore.db.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.explore.model.user.User;

import java.util.Collection;
import java.util.List;

public interface UserRepo extends PagingAndSortingRepository<User, Long> {
    List<User> findByIdIn(Collection<Long> ids);

    Page<User> findAll(Pageable pageable);

}
