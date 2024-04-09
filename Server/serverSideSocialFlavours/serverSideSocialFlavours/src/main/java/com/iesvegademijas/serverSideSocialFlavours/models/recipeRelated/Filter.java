package com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import jakarta.persistence.*;

@Entity
public class Filter {

    @Id
    private String filterType;

    @ManyToOne
    private User user;

    @ManyToOne
    private Recipe recipe;

    public Filter(){}

    public Filter(String filterType, User user, Recipe recipe) {
        this.filterType = filterType;
        this.user = user;
        this.recipe = recipe;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
