package com.iesvegademijas.serverSideSocialFlavours.models.entities;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import jakarta.persistence.*;

@Entity
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_step;
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
