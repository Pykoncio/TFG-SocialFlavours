package com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.planner;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class PlannerEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_plannerEntry;
    @ManyToOne
    @JoinColumn(name = "id_planner")
    private Planner planner;
    @Column(columnDefinition = "date")
    private Date date;
    @Column(name = "recipe_type")
    private String recipeType;
    @ManyToOne
    private Recipe recipe;

    public PlannerEntry() {}

    public PlannerEntry(Long id_plannerEntry, Planner planner, Date date, String recipeType, Recipe recipe) {
        this.id_plannerEntry = id_plannerEntry;
        this.planner = planner;
        this.date = date;
        this.recipeType = recipeType;
        this.recipe = recipe;
    }

    public Long getId_plannerEntry() {
        return id_plannerEntry;
    }

    public void setId_plannerEntry(Long id_plannerEntry) {
        this.id_plannerEntry = id_plannerEntry;
    }

    public Planner getPlanner() {
        return planner;
    }

    public void setPlanner(Planner planner) {
        this.planner = planner;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(String recipeType) {
        this.recipeType = recipeType;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
