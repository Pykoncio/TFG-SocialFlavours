package com.iesvegademijas.serverSideSocialFlavours.models.entities;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.ShoppingList;
import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_item;
    private String name;
    private int quantity;
    private boolean isChecked;
    @ManyToOne
    @JoinColumn(name = "id_shoppingList")
    private ShoppingList shoppingList;

}
