package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.entities.Ingredient;
import com.iesvegademijas.serverSideSocialFlavours.models.entities.Step;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import com.iesvegademijas.serverSideSocialFlavours.dto.RecipeDTO;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.RecipeRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(recipeRepository.findRecipesByUserId(id));
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
    public ResponseEntity<Long> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        Optional<User> user = userRepository.findById(recipeDTO.getUserId());

        if (user.isPresent()) {
            Recipe recipe = new Recipe();
            recipe.setName(recipeDTO.getName());
            recipe.setUser(user.get());
            recipe.setDescription(recipeDTO.getDescription());
            recipe.setRating(recipeDTO.getRating());
            recipe.setImagePath(recipeDTO.getImagePath());
            recipe.setPreparationTime(recipeDTO.getPreparationTime());
            recipe.setTag(recipeDTO.getTag());

            List<Ingredient> ingredients = recipeDTO.getIngredients().stream()
                    .map(ingredientName -> {
                        Ingredient ingredient = new Ingredient(ingredientName);
                        ingredient.setRecipe(recipe);
                        return ingredient;
                    })
                    .collect(Collectors.toList());
            recipe.setIngredients(ingredients);

            List<Step> steps = recipeDTO.getSteps().stream()
                    .map(stepDescription -> {
                        Step step = new Step(stepDescription);
                        step.setRecipe(recipe);
                        return step;
                    })
                    .collect(Collectors.toList());
            recipe.setSteps(steps);

            // Save the recipe
            Recipe savedRecipe = recipeRepository.save(recipe);
            return new ResponseEntity<>(savedRecipe.getId_recipe(), HttpStatus.CREATED);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAllRecipesFromUserFriends{idUser}")
    public ResponseEntity<List<Recipe>> getAllRecipesFromUserFriends(@PathVariable Long idUser) {
        List<Recipe> recipesFromFriends = new ArrayList<>();

        User user = userRepository.findById(idUser).orElse(null);

        if (user != null) {
            Set<User> approvedFriends = user.getFriends();

            for (User friend : approvedFriends) {
                recipesFromFriends.addAll(friend.getRecipes());
            }
        }


        return ResponseEntity.ok(recipesFromFriends);
    }

    @GetMapping("/getRecipe{id}")
    public ResponseEntity<Object> getRecipe(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);

        if (recipe.isPresent()) {
            return ResponseEntity.ok(recipe.get());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    @PutMapping(path = "/updateRecipe")
    public ResponseEntity<Long> recipeUpdate(@RequestBody RecipeDTO recipeDTO) {

        Optional<User> user = userRepository.findById(recipeDTO.getUserId());

        if (user.isPresent())
        {
            Recipe recipe = new Recipe();
            recipe.setId_recipe(recipeDTO.getId_recipe());
            recipe.setName(recipeDTO.getName());
            recipe.setUser(user.get());
            recipe.setDescription(recipeDTO.getDescription());
            recipe.setRating(recipeDTO.getRating());
            recipe.setImagePath(recipeDTO.getImagePath());
            recipe.setPreparationTime(recipeDTO.getPreparationTime());
            recipe.setTag(recipeDTO.getTag());

            recipeRepository.deleteStepsByRecipeId(recipe.getId_recipe());
            recipeRepository.deleteIngredientsByRecipeId(recipe.getId_recipe());

            List<Ingredient> ingredients = recipeDTO.getIngredients().stream()
                    .map(ingredientName -> {
                        Ingredient ingredient = new Ingredient(ingredientName);
                        ingredient.setRecipe(recipe);
                        return ingredient;
                    })
                    .collect(Collectors.toList());
            recipe.setIngredients(ingredients);

            List<Step> steps = recipeDTO.getSteps().stream()
                    .map(stepDescription -> {
                        Step step = new Step(stepDescription);
                        step.setRecipe(recipe);
                        return step;
                    })
                    .collect(Collectors.toList());
            recipe.setSteps(steps);

            return ResponseEntity.ok(recipeRepository.save(recipe).getId_recipe());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
