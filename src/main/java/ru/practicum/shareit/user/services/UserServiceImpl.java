package ru.practicum.shareit.user.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyUsedEmail;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepositoryImpl;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl userRepositoryImpl, ModelMapper mapper) {
        this.userRepositoryImpl = userRepositoryImpl;
        this.mapper = mapper;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepositoryImpl.getAllUsers().stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = convertDtoToUser(userDto);
        isUsedEmail(user.getEmail());
        return convertUserToDto(userRepositoryImpl.createUser(user));
    }

    @Override
    public UserDto getUserById(long userId) {
        return convertUserToDto(userRepositoryImpl.getUserById(userId));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        if (isExistUser(userId)) {
            User user = convertDtoToUser(userDto);
            isUsedEmail(user.getEmail(), userId);
            return convertUserToDto(userRepositoryImpl.updateUser(user, userId));
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void deleteUser(long userId) {
        userRepositoryImpl.deleteUser(userId);
    }

    @Override
    public boolean isExistUser(long userId) {
        return userRepositoryImpl.getUserRepo().containsKey(userId);
    }

    private User convertDtoToUser(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }

    private UserDto convertUserToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private void isUsedEmail(String email) {
        userRepositoryImpl.getUserRepo().values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .ifPresent(s -> {
                    throw new AlreadyUsedEmail(email);
                });
    }

    private void isUsedEmail(String email, long userId) {
        userRepositoryImpl.getUserRepo().values().stream()
                .filter(user -> user.getEmail().equals(email) && user.getId() != userId)
                .findFirst()
                .ifPresent(s -> {
                    throw new AlreadyUsedEmail(email);
                });
    }
}
