package com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.entities.Item;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_shoppingList;
    @ManyToOne
    private User user;
    @Column(unique = true)
    private String shoppingListName;
    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    private Set<Item> itemList = new HashSet<>();

    public ShoppingList(){}

    public ShoppingList(Long id_ShoppingList, User user, String shoppingListName,Set<Item> itemList) {
        this.id_shoppingList = id_ShoppingList;
        this.user = user;
        this.shoppingListName = shoppingListName;
        this.itemList = itemList;
    }

    public Long getId_shoppingList() {
        return id_shoppingList;
    }

    public void setId_shoppingList(Long id_ShoppingList) {
        this.id_shoppingList = id_ShoppingList;
    }

    public User getUser() {
        return user;
    }

    public String getShoppingListName() {
        return shoppingListName;
    }

    public void setShoppingListName(String shoppingListName) {
        this.shoppingListName = shoppingListName;
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
