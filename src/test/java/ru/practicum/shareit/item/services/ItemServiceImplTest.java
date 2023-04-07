package ru.practicum.shareit.item.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exceptions.InappropriateUser;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void updateItem_WithValidData_ShouldUpdateItem() {
        long itemId = 1L;
        long userId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("new name");
        itemDto.setDescription("new description");

        Item item = new Item();
        item.setId(itemId);
        item.setName("old name");
        item.setDescription("old description");
        item.setOwner(userId);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(itemId);
        itemDto1.setName("new name");
        itemDto1.setDescription("new description");

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(modelMapper.map(item, ItemDto.class)).thenReturn(itemDto1);

        ItemDto updatedItem = itemService.updateItem(itemDto, itemId, userId);

        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_WithInappropriateUser_ShouldThrowInappropriateUserException() {
        long itemId = 1L;
        long userId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("new name");
        itemDto.setDescription("new description");

        Item item = new Item();
        item.setId(itemId);
        item.setName("old name");
        item.setDescription("old description");
        item.setOwner(3L);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(InappropriateUser.class, () -> itemService.updateItem(itemDto, itemId, userId));
    }
}