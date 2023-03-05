package ru.practicum.shareit.item.repositories;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.InappropriateUser;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long countItemId = 0L;
    private final Map<Long, Item> itemRepo = new HashMap<>();

    public Map<Long, Item> getItemRepo() {
        return itemRepo;
    }

    @Override
    public List<Item> getItems(long userId) {
        return itemRepo.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Item item, long userId) {
        item.setId(++countItemId);
        item.setOwner(userId);
        itemRepo.put(countItemId, item);
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepo.get(itemId);
    }

    @Override
    public void deleteItem(long userId) {
        itemRepo.remove(userId);
    }

    @Override
    public Item updateItem(Item item, long itemId, long userId) {
        Item updatedItem = itemRepo.get(itemId);
        if (updatedItem.getOwner() != userId) {
            throw new InappropriateUser("Item has a different owner" + userId);
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        itemRepo.put(itemId, updatedItem);
        return updatedItem;
    }

    @Override
    public List<Item> searchItemByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepo.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(item -> item.getAvailable().equals(true))
                    .collect(Collectors.toList());
        }
    }
}
