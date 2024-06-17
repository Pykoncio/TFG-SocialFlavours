package com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    @Query("SELECT r FROM Recipe r WHERE r.user.id_user = :userId")
    List<Recipe> findRecipesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Step s WHERE s.recipe.id_recipe = :recipeId")
    void deleteStepsByRecipeId(@Param("recipeId") Long recipeId);

    @Modifying
    @Query("DELETE FROM Ingredient i WHERE i.recipe.id_recipe = :recipeId")
    void deleteIngredientsByRecipeId(@Param("recipeId") Long recipeId);
}
