package ru.practicum.shareit.item.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testSearchItemByText_shouldReturnSuccessResult() {
        Item item1 = new Item();
        item1.setName("iPhone");
        item1.setDescription("Apple. This is an iPhone.");
        item1.setAvailable(true);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("MacBook Pro");
        item2.setDescription("Apple. This is a MacBook Pro.");
        item2.setAvailable(false);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("iPad");
        item3.setDescription("Apple. This is an iPad.");
        item3.setAvailable(true);
        itemRepository.save(item3);

        List<Item> foundItems = itemRepository.searchItemByText("iPhone");
        assertEquals(1, foundItems.size());
        assertTrue(foundItems.contains(item1));

        foundItems = itemRepository.searchItemByText("macbook");
        assertEquals(1, foundItems.size());
        assertTrue(foundItems.contains(item2));

        foundItems = itemRepository.searchItemByText("apple");
        assertEquals(2, foundItems.size());
        assertTrue(foundItems.contains(item1));
        assertTrue(foundItems.contains(item3));
    }

    @Test
    public void testSearchItemByText_shouldReturnNegativeResult() {
        Item item1 = new Item();
        item1.setName("iPhone");
        item1.setDescription("Apple. This is an iPhone.");
        item1.setAvailable(false);
        itemRepository.save(item1);

        List<Item> foundItems = itemRepository.searchItemByText("iPhone");
        assertEquals(0, foundItems.size());
    }
}