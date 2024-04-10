package com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.planner;

import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Planner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_planner;
    @OneToOne
    private User user;
    @OneToMany(mappedBy = "planner", cascade = CascadeType.ALL)
    private Set<PlannerEntry> plannerEntries = new HashSet<>();

    public Planner(){}

    public Planner(Long id_planner, User user, Set<PlannerEntry> plannerEntries) {
        this.id_planner = id_planner;
        this.user = user;
        this.plannerEntries = plannerEntries;
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

    public Set<PlannerEntry> getPlannerEntries() {
        return plannerEntries;
    }

    public void setPlannerEntries(Set<PlannerEntry> plannerEntries) {
        this.plannerEntries = plannerEntries;
    }
}
