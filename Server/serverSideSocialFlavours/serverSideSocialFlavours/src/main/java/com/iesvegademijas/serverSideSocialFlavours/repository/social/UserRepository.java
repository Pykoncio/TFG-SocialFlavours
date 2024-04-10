package com.iesvegademijas.serverSideSocialFlavours.repository.social;

import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
