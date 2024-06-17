package com.iesvegademijas.serverSideSocialFlavours.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import jakarta.persistence.*;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ingredient;
    private String name;
    @ManyToOne
    @JsonIgnore
    private Recipe recipe;

    public Ingredient(){}

    public Ingredient(String ingredientName) {
        this.name = ingredientName;
    }

    public Long getId_ingredient() {
        return id_ingredient;
    }

    public void setId_ingredient(Long id_ingredient) {
        this.id_ingredient = id_ingredient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


}
