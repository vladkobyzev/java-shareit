package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private Long owner;
}
