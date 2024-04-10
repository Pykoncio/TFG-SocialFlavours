package com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.entities.Ingredient;
import com.iesvegademijas.serverSideSocialFlavours.models.entities.Step;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Recipe {
    @Id
    @GeneratedValue
    private Long id_recipe;
    @ManyToOne
    private User user;

    private String name;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private Set<Ingredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private Set<Step> steps = new HashSet<>();

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL)
    private Set<Filter> filters = new HashSet<>();
    @Column(columnDefinition = "date")
    private Date creationDate;

    public Recipe(){}

    public Recipe(Long id_recipe, User user, String name, Date creationDate) {
        this.id_recipe = id_recipe;
        this.user = user;
        this.name = name;
        this.creationDate = creationDate;
    }

    public Long getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(Long id_recipe) {
        this.id_recipe = id_recipe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<Step> getSteps() {
        return steps;
    }

    public void setSteps(Set<Step> steps) {
        this.steps = steps;
    }

    public Set<Filter> getFilters() {
        return filters;
    }

    public void setFilters(Set<Filter> filters) {
        this.filters = filters;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
