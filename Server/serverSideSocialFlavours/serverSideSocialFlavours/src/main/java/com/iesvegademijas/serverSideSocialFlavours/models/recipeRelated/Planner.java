package com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Planner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_planner;
    @OneToOne
    private User user;
    @ManyToMany
    private Set<Recipe> recipeList = new HashSet<>();

    public Planner(){}

    public Planner(Long id_planner, User user, Set<Recipe> recipeList) {
        this.id_planner = id_planner;
        this.user = user;
        this.recipeList = recipeList;
    }

    public Long getId_planner() {
        return id_planner;
    }

    public void setId_planner(Long id_planner) {
        this.id_planner = id_planner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(Set<Recipe> recipeList) {
        this.recipeList = recipeList;
    }
}
