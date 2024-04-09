package com.iesvegademijas.serverSideSocialFlavours.models.dto;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.ShoppingList;
import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    private String name;
    private int quantity;
    private boolean isChecked;
    @ManyToOne
    @JoinColumn(name = "id_shoppingList")
    private ShoppingList shoppingList;

}
