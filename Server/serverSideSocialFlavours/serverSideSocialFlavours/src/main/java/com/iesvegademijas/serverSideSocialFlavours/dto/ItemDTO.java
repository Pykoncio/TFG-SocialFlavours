package com.iesvegademijas.serverSideSocialFlavours.dto;

public class ItemDTO {
    private long id_item;
    private String name;
    private int quantity;
    private boolean isChecked;
    private long id_shoppingList;

    public ItemDTO(long id_item, String name, int quantity, boolean isChecked, long id_shoppingList) {
        this.id_item = id_item;
        this.name = name;
        this.quantity = quantity;
        this.isChecked = isChecked;
        this.id_shoppingList = id_shoppingList;
    }

    public long getId_item() {
        return id_item;
    }

    public void setId_item(long id_item) {
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public long getId_shoppingList() {
        return id_shoppingList;
    }

    public void setId_shoppingList(long id_shoppingList) {
        this.id_shoppingList = id_shoppingList;
    }
}
