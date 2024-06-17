package com.iesvegademijas.serverSideSocialFlavours.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import jakarta.persistence.*;

@Entity
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_step;
    private String step;
    @ManyToOne
    @JsonIgnore
    private Recipe recipe;

    public Step(){}

    public Step(String ingredientName) {
        this.step = ingredientName;
    }

    public Long getId_step() {
        return id_step;
    }

    public void setId_step(Long id_step) {
        this.id_step = id_step;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String ingredientName) {
        this.step = ingredientName;
    }
}
