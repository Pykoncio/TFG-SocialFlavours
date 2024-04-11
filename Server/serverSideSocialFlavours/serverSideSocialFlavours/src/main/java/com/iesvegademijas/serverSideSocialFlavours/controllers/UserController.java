package com.iesvegademijas.serverSideSocialFlavours.controllers;

import com.iesvegademijas.serverSideSocialFlavours.dto.LoginDTO;
import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.Filter;
import com.iesvegademijas.serverSideSocialFlavours.models.social.FriendshipRequest;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.FilterRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "/userRegister")
    public ResponseEntity<Long> userRegistration(@RequestBody User user) {

        User userAlreadyExisting = userRepository.findByUsername(user.getUsername());

        if (userAlreadyExisting != null)
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        else
        {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));

            User userCreated = userRepository.save(user);

            // TESTING
            System.out.println(userCreated.getPassword());

            return ResponseEntity.status(HttpStatus.CREATED).body(userCreated.getId_user());
        }
    }

    @GetMapping(path = "/userLogin")
    public ResponseEntity<User> userLogin(@RequestBody LoginDTO loginCredentials)
    {
        User userSearched = userRepository.findByUsername(loginCredentials.getUsername());

        if (userSearched != null)
        {
            String password = loginCredentials.getPassword();
            String encodedPassword = userSearched.getPassword();

            boolean isPasswordRight = passwordEncoder.matches(password, encodedPassword);

            if (isPasswordRight) {
                return ResponseEntity.ok(userSearched);
            }
            else
            {
                return ResponseEntity.badRequest().build();
            }
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers()
    {
        List<User> result = new ArrayList<>();

        Iterable<User> users = userRepository.findAll();

        for(User user : users)
        {
            result.add(user);
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping(path = "/userUpdate")
    public ResponseEntity<Long> userUpdate(@RequestBody User user) {
        Optional<User> updatedUser = Optional.of(userRepository.save(user));
        return ResponseEntity.ok(updatedUser.get().getId_user());
    }

    @DeleteMapping(path = "/userDelete{id}")
    public ResponseEntity<Object> userDelete(@PathVariable Long id) {
        Optional<User> deletedUser = userRepository.findById(id);

        if (deletedUser.isPresent()) {
            userRepository.delete(deletedUser.get());
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/userPendingFriendshipRequests")
    public ResponseEntity<List<FriendshipRequest>> userPendingFriendshipRequests(@RequestParam Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<FriendshipRequest> result = new ArrayList<>();

            Iterable<FriendshipRequest> friends = user.get().getReceivedFriendshipRequests();

            for (FriendshipRequest friendshipRequest : friends) {
                result.add(friendshipRequest);
            }

            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/userFriends")
    public ResponseEntity<List<User>> userFriends(@RequestParam Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<User> result = new ArrayList<>();

            Iterable<User> friends = user.get().getFriends();

            for (User friend : friends) {
                result.add(friend);
            }

            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

}
