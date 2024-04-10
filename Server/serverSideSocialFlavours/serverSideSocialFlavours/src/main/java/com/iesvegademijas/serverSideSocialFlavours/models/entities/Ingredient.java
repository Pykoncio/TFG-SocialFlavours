package com.iesvegademijas.serverSideSocialFlavours.models.entities;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import jakarta.persistence.*;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ingredient;
    private String name;
    @ManyToOne
    private Recipe recipe;

    public Ingredient(){}

    public Ingredient(String ingredientName) {
        this.name = ingredientName;
    }

    public String getIngredientName() {
        return name;
    }

    public void setIngredientName(String ingredientName) {
        this.name = ingredientName;
    }


}
