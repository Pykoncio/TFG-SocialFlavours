package com.iesvegademijas.serverSideSocialFlavours.controllers;

import com.iesvegademijas.serverSideSocialFlavours.dto.LoginDTO;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {this.userRepository = userRepository;}

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

    @PostMapping(path = "/userLogin")
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


}
