package ru.practicum.shareit.item;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ModelMapper mapper;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ModelMapper mapper, ItemRepository itemRepository, UserService userService) {
        this.mapper = mapper;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        return itemRepository.searchItemByText(text).stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.getItems(userId).stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (userService.isExistUser(userId)) {
            Item item = convertDtoToItem(itemDto);
            return convertItemToDto(itemRepository.createItem(item, userId));
        } else {
            throw new EntityNotFound("User not found: " + userId);
        }
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return convertItemToDto(itemRepository.getItemById(itemId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        if (isExistItem(itemId)) {
            Item itemUpdate = convertDtoToItem(itemDto);
            return convertItemToDto(itemRepository.updateItem(itemUpdate, itemId, userId));
        } else {
            throw new EntityNotFound("Item not found:" + itemId);
        }
    }

    @Override
    public void deleteItem(long userId) {
        itemRepository.deleteItem(userId);
    }

    private Item convertDtoToItem(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }

    private ItemDto convertItemToDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }

    public boolean isExistItem(long itemId) {
        return itemRepository.getItemRepo().containsKey(itemId);
    }
}
