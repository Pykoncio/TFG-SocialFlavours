package com.iesvegademijas.serverSideSocialFlavours.controllers.entities;

import com.iesvegademijas.serverSideSocialFlavours.dto.ItemDTO;
import com.iesvegademijas.serverSideSocialFlavours.dto.ShoppingListDTO;
import com.iesvegademijas.serverSideSocialFlavours.models.entities.Item;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.ShoppingList;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.entities.ItemRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.ShoppingListRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/itemapi")
public class ItemController {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository, UserRepository userRepository, ShoppingListRepository shoppingListRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.shoppingListRepository = shoppingListRepository;
    }

    @GetMapping("/getItem{id}")
    public ResponseEntity<Object> getRecipe(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path = "/createItem")
    public ResponseEntity<Long> createShoppingList(@RequestBody ItemDTO itemDTO) {
        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(itemDTO.getId_shoppingList());

        if (shoppingList.isPresent()) {
            Item item = new Item();
            item.setShoppingList(shoppingList.get());
            item.setName(itemDTO.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setChecked(itemDTO.isChecked());

            Item savedItem = itemRepository.save(item);
            return new ResponseEntity<>(item.getId_item(), HttpStatus.CREATED);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/getAllItemsFromShoppingList{id}")
    public ResponseEntity<List<Item>> getAllItemsFromShoppingList(@PathVariable Long id) {
        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(id);

        if (shoppingList.isPresent()) {
            List<Item> result = new ArrayList<>();

            Iterable<Item> items = shoppingList.get().getItemList();

            for (Item item : items) {
                result.add(item);
            }

            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path = "/itemUpdate")
    public ResponseEntity<Long> userUpdate(@RequestBody ItemDTO itemDTO) {

        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(itemDTO.getId_shoppingList());

        if (shoppingList.isPresent()) {
            Item item = new Item();
            item.setId_item(itemDTO.getId_item());
            item.setName(itemDTO.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setChecked(itemDTO.isChecked());
            item.setShoppingList(shoppingList.get());

            Item savedItem = itemRepository.save(item);
            return ResponseEntity.ok(savedItem.getId_item());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/deleteItem{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id)
    {
        Optional<Item> deletedItem = itemRepository.findById(id);

        if (deletedItem.isPresent()) {
            itemRepository.delete(deletedItem.get());
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
