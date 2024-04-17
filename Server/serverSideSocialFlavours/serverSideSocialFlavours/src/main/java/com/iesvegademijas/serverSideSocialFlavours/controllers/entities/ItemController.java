package com.iesvegademijas.serverSideSocialFlavours.controllers.entities;

import com.iesvegademijas.serverSideSocialFlavours.repository.entities.ItemRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/itemapi")
public class ItemController {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }
}
