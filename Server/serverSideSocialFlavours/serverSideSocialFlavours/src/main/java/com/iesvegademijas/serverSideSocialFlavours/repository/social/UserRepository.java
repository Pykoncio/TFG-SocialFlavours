package com.iesvegademijas.serverSideSocialFlavours.repository.social;

import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
