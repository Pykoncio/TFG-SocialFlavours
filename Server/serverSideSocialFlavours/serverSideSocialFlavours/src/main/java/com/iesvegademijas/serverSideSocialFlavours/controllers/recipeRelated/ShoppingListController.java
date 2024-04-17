package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.controllers.social.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shoppinglistapi")
public class ShoppingListController {

    private final ShoppingListController shoppingListController;
    private final UserController userController;

    @Autowired
    public ShoppingListController(ShoppingListController shoppingListController, UserController userController) {
        this.shoppingListController = shoppingListController;
        this.userController = userController;
    }
}
