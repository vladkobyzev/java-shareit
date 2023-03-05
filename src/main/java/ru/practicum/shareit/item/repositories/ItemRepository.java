package ru.practicum.shareit.item.repositories;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getItems(long userId);

    Item createItem(Item item, long userId);

    Item getItemById(long itemId);

    void deleteItem(long userId);

    Item updateItem(Item item, long itemId, long userId);

    List<Item> searchItemByText(String text);
}
