package com.iesvegademijas.serverSideSocialFlavours.dto;


public class UserDTO {
    private Long id_user;
    private String username;
    private String password;
    private String email;

    public UserDTO() {}

    public UserDTO(Long id_user, String username, String password, String email) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
        this.email = email;
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
}
