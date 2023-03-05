package ru.practicum.shareit.item.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.item.repositories.ItemRepositoryImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ModelMapper mapper;
    private final ItemRepositoryImpl itemRepositoryImpl;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ModelMapper mapper, ItemRepositoryImpl itemRepositoryImpl, UserService userService) {
        this.mapper = mapper;
        this.itemRepositoryImpl = itemRepositoryImpl;
        this.userService = userService;
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        return itemRepositoryImpl.searchItemByText(text).stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepositoryImpl.getItems(userId).stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (userService.isExistUser(userId)) {
            Item item = convertDtoToItem(itemDto);
            return convertItemToDto(itemRepositoryImpl.createItem(item, userId));
        } else {
            throw new EntityNotFound("User not found: " + userId);
        }
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return convertItemToDto(itemRepositoryImpl.getItemById(itemId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        if (isExistItem(itemId)) {
            Item itemUpdate = convertDtoToItem(itemDto);
            return convertItemToDto(itemRepositoryImpl.updateItem(itemUpdate, itemId, userId));
        } else {
            throw new EntityNotFound("Item not found:" + itemId);
        }
    }

    @Override
    public void deleteItem(long userId) {
        itemRepositoryImpl.deleteItem(userId);
    }

    private Item convertDtoToItem(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }

    private ItemDto convertItemToDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }

    public boolean isExistItem(long itemId) {
        return itemRepositoryImpl.getItemRepo().containsKey(itemId);
    }
}
