package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {
    private Long countUserId = 0L;
    private final Map<Long, User> userRepo = new HashMap<>();

    public Map<Long, User> getUserRepo() {
        return userRepo;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepo.values());
    }

    public User createUser(User user) {
        user.setId(++countUserId);
        userRepo.put(countUserId, user);
        return user;
    }

    public User getUserById(long userId) {
        return userRepo.get(userId);
    }

    public void deleteUser(long userId) {
        userRepo.remove(userId);
    }

    public User updateUser(User user, long userId) {
        User updatedUser = userRepo.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        userRepo.put(userId, updatedUser);
        return updatedUser;
    }
}
