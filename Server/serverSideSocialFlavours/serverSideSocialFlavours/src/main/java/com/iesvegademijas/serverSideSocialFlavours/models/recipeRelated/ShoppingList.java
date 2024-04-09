package com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.dto.Item;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ShoppingList;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    private Set<Item> itemList = new HashSet<>();

    public ShoppingList(){}

    public ShoppingList(Long id_ShoppingList, User user, Set<Item> itemList) {
        this.id_ShoppingList = id_ShoppingList;
        this.user = user;
        this.itemList = itemList;
    }

    public Long getId_ShoppingList() {
        return id_ShoppingList;
    }

    public void setId_ShoppingList(Long id_ShoppingList) {
        this.id_ShoppingList = id_ShoppingList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Item> getItemList() {
        return itemList;
    }

    public void setItemList(Set<Item> itemList) {
        this.itemList = itemList;
    }
}
