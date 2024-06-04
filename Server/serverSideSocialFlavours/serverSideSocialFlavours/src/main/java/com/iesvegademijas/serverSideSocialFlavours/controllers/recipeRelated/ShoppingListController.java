package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.dto.ShoppingListDTO;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.ShoppingList;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
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
@RequestMapping("/shoppinglistapi")
public class ShoppingListController {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShoppingListController(ShoppingListRepository shoppingListRepository, UserRepository userRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(path = "/createShoppingList")
    public ResponseEntity<Long> createShoppingList(@RequestBody ShoppingListDTO shoppingListDTO) {
        Optional<User> user = userRepository.findById(shoppingListDTO.getId_user());

        if (user.isPresent()) {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setShoppingListName(shoppingListDTO.getName());
            shoppingList.setUser(user.get());
            shoppingList.setItemList(new HashSet<>());

            ShoppingList savedShoppingList = shoppingListRepository.save(shoppingList);
            return new ResponseEntity<>(savedShoppingList.getId_shoppingList(), HttpStatus.CREATED);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/getAllShoppingListsFromUser{id}")
    public ResponseEntity<List<ShoppingList>> getAllShoppingListsFromUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            List<ShoppingList> result = new ArrayList<>();

            Iterable<ShoppingList> shoppingLists = user.get().getShoppingLists();

            for (ShoppingList shoppingList : shoppingLists) {
                result.add(shoppingList);
            }

            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/deleteShoppingList{id}")
    public ResponseEntity<Object> deleteShoppingList(@PathVariable Long id)
    {
        Optional<ShoppingList> deletedShoppingList = shoppingListRepository.findById(id);

        if (deletedShoppingList.isPresent()) {
            shoppingListRepository.delete(deletedShoppingList.get());
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
