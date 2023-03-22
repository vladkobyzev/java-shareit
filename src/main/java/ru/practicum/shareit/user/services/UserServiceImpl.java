package ru.practicum.shareit.user.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyUsedEmail;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Transactional(readOnly = true)
    @Override
    public UserDto getUserDtoById(long userId) {
        return convertUserToDto(getUserById(userId));
    }
    public User getUserById(long userId) {
        return userRepository.findById(userId).
                orElseThrow(() -> new EntityNotFound("User not found: " + userId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = convertDtoToUser(userDto);
        return convertUserToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        isExistUser(userId);
        User user = userRepository.findById(userId).get();
        isUsedEmail(user.getEmail(), userId);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return convertUserToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void isExistUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFound("User not found: " + userId);
        }
    }

    private User convertDtoToUser(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }

    private UserDto convertUserToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private void isUsedEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new AlreadyUsedEmail(email);
        }
    }

    private void isUsedEmail(String email, long userId) {
        if(userRepository.findByEmail(email).getId() != userId) {
            throw new AlreadyUsedEmail(email);
        }
    }
}
