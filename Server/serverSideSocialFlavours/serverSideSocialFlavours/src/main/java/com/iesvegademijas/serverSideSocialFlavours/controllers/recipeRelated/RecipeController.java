package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import com.iesvegademijas.serverSideSocialFlavours.models.social.FriendshipRequest;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.RecipeRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipeapi")
public class RecipeController {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeController(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(path = "/getAllRecipesFromUser{id}")
    public ResponseEntity<List<Recipe>> getAllRecipesFromUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            List<Recipe> result = new ArrayList<>();

            Iterable<Recipe> recipes = user.get().getRecipes();

            for (Recipe recipe : recipes) {
                result.add(recipe);
            }

            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/deleteRecipe{id}")
    public ResponseEntity<Object> deleteRecipe(@PathVariable Long id)
    {
        Optional<Recipe> deletedRecipe = recipeRepository.findById(id);

        if (deletedRecipe.isPresent()) {
            recipeRepository.delete(deletedRecipe.get());
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
