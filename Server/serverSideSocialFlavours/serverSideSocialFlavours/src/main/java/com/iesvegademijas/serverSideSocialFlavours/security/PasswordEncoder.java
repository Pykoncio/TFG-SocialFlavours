package com.iesvegademijas.serverSideSocialFlavours.security;

import java.util.Base64;

public class PasswordEncoder {

    public PasswordEncoder() {}

    public String encode(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public String decode(String password) {
        return new String(Base64.getDecoder().decode(password));
    }

    public boolean matches(String password, String encodedPassword) {
        return decode(encodedPassword).equals(password);
    }

}
