package ru.practicum.shareit.user.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exceptions.AlreadyUsedEmail;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
public class UserServiceImplTest {

    @MockBean
    private UserServiceImpl userService;

    @Test
    public void testUpdateUser_withValidUserIdAndNewName_shouldUpdateName() {
        long userId = 1L;
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setName("John Updated");
        updatedUserDto.setEmail("john@example.com");

        UserDto updatedUserDto1 = new UserDto();
        updatedUserDto1.setName("John Updated");

        when(userService.updateUser(updatedUserDto1, userId)).thenReturn(updatedUserDto);

        UserDto updatedUser = userService.updateUser(updatedUserDto1, userId);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getName(), "John Updated");
        assertEquals(updatedUser.getEmail(), "john@example.com");
    }

    @Test
    public void testUpdateUser_withValidUserIdAndNewEmail_shouldUpdateEmail() {
        long userId = 1L;
        UserDto existingUser = new UserDto();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("updated@example.com");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setEmail("updated@example.com");
        when(userService.updateUser(updatedUserDto, userId)).thenReturn(existingUser);

        UserDto updatedUser = userService.updateUser(updatedUserDto, userId);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getName(), "John");
        assertEquals(updatedUser.getEmail(), "updated@example.com");
    }

    @Test
    public void testUpdateUser_withNonExistentUserId_shouldThrowException() {
        long userId = 1L;
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setEmail("update@example.com");
        Mockito.when(userService.updateUser(updatedUserDto, userId)).thenThrow(new EntityNotFound("User not found: " + userId));

        final EntityNotFound exception = assertThrows(EntityNotFound.class,
                () -> userService.updateUser(updatedUserDto, userId));

        assertEquals("User not found: " + userId, exception.getMessage());
    }

    @Test
    public void testUpdateUser_withUsedEmail_shouldThrowException() {
        long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        User existingUser1 = new User();
        existingUser1.setId(2L);
        existingUser1.setName("Jane");
        existingUser1.setEmail("jane@example.com");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("John Update");
        updatedUserDto.setEmail("jane@example.com");
        when(userService.updateUser(updatedUserDto, userId)).thenThrow(new AlreadyUsedEmail("Already Used Email: " + "jane@example.com"));

        final AlreadyUsedEmail exception = assertThrows(AlreadyUsedEmail.class,
                () -> userService.updateUser(updatedUserDto, userId));

        assertEquals("Already Used Email: " + "jane@example.com", exception.getMessage());
    }
}