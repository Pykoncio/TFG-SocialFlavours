package com.iesvegademijas.serverSideSocialFlavours.models.dto;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Step {
    @Id
    private String step;

    @ManyToOne
    private Recipe recipe;

    public Step(){}

    public Step(String ingredientName) {
        this.step = ingredientName;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String ingredientName) {
        this.step = ingredientName;
    }
}
