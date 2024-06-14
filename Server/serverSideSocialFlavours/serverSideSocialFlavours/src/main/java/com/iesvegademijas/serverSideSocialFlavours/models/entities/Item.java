package com.iesvegademijas.serverSideSocialFlavours.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.ShoppingList;
import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_item;
    private String name;
    private int quantity;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_shoppingList")
    private ShoppingList shoppingList;

    public Item(){}

    public Item(Long id_item, String name, int quantity, ShoppingList shoppingList) {
        this.id_item = id_item;
        this.name = name;
        this.quantity = quantity;
        this.shoppingList = shoppingList;
    }

    public Long getId_item() {
        return id_item;
    }

    public void setId_item(Long id_item) {
        this.id_item = id_item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }
}
