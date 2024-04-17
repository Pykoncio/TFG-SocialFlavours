package com.iesvegademijas.serverSideSocialFlavours.controllers.social;

import com.iesvegademijas.serverSideSocialFlavours.dto.UserDTO;
import com.iesvegademijas.serverSideSocialFlavours.models.social.FriendshipRequest;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import com.iesvegademijas.serverSideSocialFlavours.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/userapi")
public class UserController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @Autowired
    public UserController(UserRepository userRepository) {this.userRepository = userRepository;}

    @PostMapping(path = "/register")
    public ResponseEntity<Long> userRegistration(@RequestBody UserDTO userDTO) {

        User userAlreadyExisting = userRepository.findByUsername(userDTO.getUsername());

        if (userAlreadyExisting != null)
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build(); // There is a user with the same username
        }
        else
        {
            userDTO.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));

            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());

            User userCreated = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(userCreated.getId_user());
        }
    }

    @GetMapping(path = "/userLogin")
    public ResponseEntity<User> userLogin(@RequestBody UserDTO userDTO)
    {
        User userSearched = userRepository.findByUsername(userDTO.getUsername());

        if (userSearched != null)
        {
            String password = userDTO.getPassword();
            String encodedPassword = userSearched.getPassword();

            boolean isPasswordRight = passwordEncoder.matches(password, encodedPassword);

            if (isPasswordRight) {
                return ResponseEntity.ok(userSearched);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build(); // Password Incorrect
            }
        }
        else
        {
            return ResponseEntity.notFound().build(); // User Not Found
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

    @GetMapping(path = "/userInboxFriendshipRequests")
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