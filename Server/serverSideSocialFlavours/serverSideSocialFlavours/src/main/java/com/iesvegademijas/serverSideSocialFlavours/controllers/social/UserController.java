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

    @GetMapping(path = "/userLogin/{username}_{password}")
    public ResponseEntity<User> userLogin(@PathVariable String username, @PathVariable String password)
    {
        User userSearched = userRepository.findByUsername(username);

        if (userSearched != null)
        {

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

    @GetMapping(path = "/userFriends{id}")
    public ResponseEntity<List<User>> userFriends(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

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

    @DeleteMapping(path = "/userDeleteFriendship{userId}/{friendUsername}")
    public ResponseEntity<Object> userDeleteFriendship(@PathVariable Long userId,
                                                       @PathVariable String friendUsername) {
        Optional<User> userOptional = userRepository.findById(userId);
        User friend = userRepository.findByUsername(friendUsername);

        if (userOptional.isPresent() && friend != null) {
            User user = userOptional.get();

            // Check if there is an existing friendship request between these two users
            FriendshipRequest friendshipRequest = null;
            for (FriendshipRequest request : user.getReceivedFriendshipRequests()) {
                if (request.getSender().equals(friend) && request.getStatus().equals(FriendshipRequest.Status.APPROVED.toString())) {
                    friendshipRequest = request;
                    break;
                }
            }
            if (friendshipRequest == null) {
                for (FriendshipRequest request : user.getSentFriendshipRequests()) {
                    if (request.getReceiver().equals(friend) && request.getStatus().equals(FriendshipRequest.Status.APPROVED.toString())) {
                        friendshipRequest = request;
                        break;
                    }
                }
            }

            if (friendshipRequest != null) {
                user.getReceivedFriendshipRequests().remove(friendshipRequest);
                friend.getSentFriendshipRequests().remove(friendshipRequest);
                userRepository.save(user);
                userRepository.save(friend);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friendship not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or friend not found");
        }
    }


}
