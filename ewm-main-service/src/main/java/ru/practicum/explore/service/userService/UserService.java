package ru.practicum.explore.service.userService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.db.repo.UserRepo;
import ru.practicum.explore.model.exceptions.ConflictException;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.model.user.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        try {
            var userToCreate = userMapper.fromDto(userDto);
            User createdUser = userRepo.save(userToCreate);
            return userMapper.toDto(createdUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("User with this email is already registered");
        }
    }

    public void deleteUserById(long userId) {
        userRepo.deleteById(userId);
    }

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids.isEmpty()) {
            int page = from % size > 0 ? (from / size) + 1 : from / size;
            PageRequest pageRequest = PageRequest.of(page, size);
            return userRepo.findAll(pageRequest).toList().stream().map(userMapper::toDto).collect(Collectors.toList());
        } else {
            return userRepo.findByIdIn(ids).stream().map(userMapper::toDto).collect(Collectors.toList());
        }
    }
}
