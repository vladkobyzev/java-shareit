package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    void deleteUser(long userId);

    UserDto getUserById(long id);

    boolean isExistUser(long userId);
}
