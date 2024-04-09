package com.iesvegademijas.serverSideSocialFlavours.models.dto;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Ingredient {
    @Id
    private String ingredientName;

    @ManyToOne
    private Recipe recipe;

    public Ingredient(){}

    public Ingredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }


}
