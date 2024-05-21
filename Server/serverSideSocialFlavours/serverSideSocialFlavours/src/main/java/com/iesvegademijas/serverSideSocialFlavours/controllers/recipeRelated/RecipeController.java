package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.entities.Ingredient;
import com.iesvegademijas.serverSideSocialFlavours.models.entities.Step;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.RecipeDTO;
import com.iesvegademijas.serverSideSocialFlavours.models.social.FriendshipRequest;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.RecipeRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

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

    @PostMapping("/createRecipe")
    public ResponseEntity<Object> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        Optional<User> user = userRepository.findById(recipeDTO.getUserId());

        if (user.isPresent()) {
            // Mapping the DTO to the Recipe entity
            Recipe recipe = new Recipe();
            recipe.setName(recipeDTO.getName());
            recipe.setDescription(recipeDTO.getDescription());
            recipe.setRating(recipeDTO.getRating());
            recipe.setImagePath(recipeDTO.getImagePath());
            recipe.setPreparationTime(recipeDTO.getPreparationTime());
            recipe.setTag(recipeDTO.getTag());
            try {
                recipe.setCreationDate(recipeDTO.getDate());
            } catch (ParseException e) {
                recipe.setCreationDate(new Date());
            }

            // Convert ingredients and steps from DTO to Ingredient and Step objects
            List<Ingredient> ingredients = recipeDTO.getIngredients().stream()
                    .map(Ingredient::new)
                    .toList();
            recipe.setIngredients(new HashSet<>(ingredients));

            List<Step> steps = recipeDTO.getSteps().stream()
                    .map(Step::new)
                    .toList();
            recipe.setSteps(new HashSet<>(steps));

            // Save the recipe
            Recipe savedRecipe = recipeRepository.save(recipe);
            return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAllRecipesFromUserFriends{idUser}")
    public ResponseEntity<List<Recipe>> getAllRecipesFromUserFriends(@PathVariable Long idUser) {
        Optional<User> user = userRepository.findById(idUser);

        if (user.isPresent())
        {
            List<User> friends = new ArrayList<>(user.get().getFriends());
            List<Recipe> recipes = new ArrayList<>();

            for(User friend : friends)
            {
                recipes.addAll(friend.getRecipes());
            }

            return ResponseEntity.ok(recipes);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
