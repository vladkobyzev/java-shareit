package ru.practicum.shareit.request.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.RequestRepository;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
class ItemRequestServiceImplTest {
    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRequestService requestService;

    @MockBean
    private ModelMapper modelMapper;


    @Test
    void testCreateRequestSuccess() {
        long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test Description");
        requestDto.setCreated(LocalDateTime.now());
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setOwner(userId);
        request.setCreated(requestDto.getCreated());


        doNothing().when(userService).isExistUser(userId);
        when(requestRepository.save(request)).thenReturn(request);
        when(requestService.createRequest(requestDto, userId)).thenReturn(requestDto);


        ItemRequestDto result = requestService.createRequest(requestDto, userId);

        assertEquals(requestDto.getDescription(), result.getDescription());
        assertNotNull(result.getId());
        assertNotNull(result.getCreated());
    }

    @Test
    void testCreateRequestWithInvalidUserId() {
        long userId = 1000L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test Description");

        when(requestService.createRequest(requestDto, userId)).thenThrow(new EntityNotFound("Entity not found"));

        assertThrows(EntityNotFound.class, () -> requestService.createRequest(requestDto, userId));
    }

    @Test
    public void testGetRequestById() {
        long userId = 1;
        long requestId = 1;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(requestId);
        ItemRequest request = new ItemRequest();
        request.setOwner(userId);

        doNothing().when(userService).isExistUser(userId);
        when(requestService.getRequestById(requestId, userId)).thenReturn(requestDto);
        requestRepository.save(request);
        ItemRequestDto actualResultDto = requestService.getRequestById(requestId, userId);

        assertNotNull(actualResultDto);
        assertEquals(requestId, actualResultDto.getId());
    }

    @Test
    public void testGetRequestByIdInvalidUser() {
        long userId = 1000;
        long requestId = 1;
        ItemRequest request = new ItemRequest();
        request.setOwner(userId);

        when(requestService.getRequestById(requestId, userId)).thenThrow(new EntityNotFound("Entity not found"));
        assertThrows(EntityNotFound.class, () -> requestService.getRequestById(requestId, userId));

    }

    @Test
    public void testGetRequestByIdInvalidRequest() {
        long userId = 1;
        long requestId = -1;
        when(requestService.getRequestById(requestId, userId)).thenThrow(new EntityNotFound("Entity not found"));

        assertThrows(EntityNotFound.class, () -> requestService.getRequestById(requestId, userId));
    }

    @Test
    public void testGetOwnerRequests() {
        long ownerId = 1;
        List<ItemRequestDto> requests = new ArrayList<>();
        requests.add(new ItemRequestDto());
        requests.add(new ItemRequestDto());

        doNothing().when(userService).isExistUser(ownerId);
        when(requestService.getOwnerRequests(ownerId)).thenReturn(requests);

        List<ItemRequestDto> result = requestService.getOwnerRequests(ownerId);
        assertEquals(requests.size(), result.size());
    }


    @Test
    public void testGetOwnerRequestsInvalidUser() {
        long ownerId = -1;
        when(requestService.getOwnerRequests(ownerId)).thenThrow(new EntityNotFound("Entity not found"));

        assertThrows(EntityNotFound.class, () -> requestService.getOwnerRequests(ownerId));

    }

    @Test
    public void testGetUserRequests_whenNoPagination() {
        Long userId = 1L;
        List<ItemRequest> requests = Arrays.asList(new ItemRequest(), new ItemRequest());
        when(requestRepository.findAllByOwner(userId)).thenReturn(requests);
        when(requestService.getUserRequests(userId, null, null)).thenAnswer(invocationOnMock -> {
            return requestRepository.findAllByOwner(userId).stream()
                    .map(itemRequest -> modelMapper.map(itemRequest, ItemRequestDto.class))
                    .collect(Collectors.toList());
        });
        List<ItemRequestDto> result = requestService.getUserRequests(userId, null, null);

        verify(requestRepository).findAllByOwner(userId);
        assertEquals(requests.size(), result.size());
    }
}