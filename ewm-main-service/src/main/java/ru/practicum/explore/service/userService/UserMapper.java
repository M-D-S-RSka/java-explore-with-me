package ru.practicum.explore.service.userService;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.model.user.UserDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface UserMapper {
    User fromDto(UserDto userDto);

    UserDto toDto(User user);
}
