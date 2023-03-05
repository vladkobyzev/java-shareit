package ru.practicum.shareit.user.repositories;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User createUser(User user);

    User getUserById(long userId);

    void deleteUser(long userId);

    User updateUser(User user, long userId);
}
