package com.iesvegademijas.serverSideSocialFlavours.models.social;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.planner.Planner;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Recipe;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.ShoppingList;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private Set<FriendshipRequest> receivedFriendshipRequests = new HashSet<>();
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private Set<FriendshipRequest> sentFriendshipRequests = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ShoppingList> shoppingLists = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Recipe> recipes = new HashSet<>();

    public User(){}

    public User(Long idUser, String username, String password, String email) {
        this.id_user = idUser;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Set<User> getFriends() {
        Set<User> friends = new HashSet<>();
        for (FriendshipRequest request : receivedFriendshipRequests) {
            if (request.getStatus().equals(FriendshipRequest.Status.APPROVED.toString())) {
                friends.add(request.getSender());
            }
        }
        for (FriendshipRequest request : sentFriendshipRequests) {
            if (request.getStatus().equals(FriendshipRequest.Status.APPROVED.toString())) {
                friends.add(request.getReceiver());
            }
        }
        return friends;
    }

    public Long getId_user() {
        return id_user;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<FriendshipRequest> getReceivedFriendshipRequests() {
        return receivedFriendshipRequests;
    }

    public void setReceivedFriendshipRequests(Set<FriendshipRequest> receivedFriendshipRequests) {
        this.receivedFriendshipRequests = receivedFriendshipRequests;
    }

    public Set<FriendshipRequest> getSentFriendshipRequests() {
        return sentFriendshipRequests;
    }

    public void setSentFriendshipRequests(Set<FriendshipRequest> sentFriendshipRequests) {
        this.sentFriendshipRequests = sentFriendshipRequests;
    }

    public Set<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(Set<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }
}
