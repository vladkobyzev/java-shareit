package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    List<ItemDto> getItems(long userId);

    void deleteItem(long id);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> searchItemByText(String text);
}
