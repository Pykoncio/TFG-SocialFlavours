package com.iesvegademijas.serverSideSocialFlavours.security;

import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mailapi")
public class MailController {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @PostMapping("/send/{username}")
    public ResponseEntity<String> sendMail(@PathVariable String username) {

        User user = userRepository.findByUsername(username);

        if (user != null) // This means the user provided exists
        {
            emailSenderService.SendEmail(user.getEmail(), "Password Recovery", passwordEncoder.decode(user.getPassword()));
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

}
