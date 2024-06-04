package com.iesvegademijas.serverSideSocialFlavours.dto;

public class ShoppingListDTO {
    private Long id_user;
    private String name;

    public ShoppingListDTO(Long id_user, String name) {
        this.id_user = id_user;
        this.name = name;
    }

    public Long getId_user() {
        return id_user;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
