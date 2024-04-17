package com.iesvegademijas.serverSideSocialFlavours.controllers.entities;

import com.iesvegademijas.serverSideSocialFlavours.repository.entities.StepRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stepapi")
public class StepController {
    private final StepRepository stepRepository;
    private final UserRepository userRepository;

    @Autowired
    public StepController(StepRepository stepRepository, UserRepository userRepository) {
        this.stepRepository = stepRepository;
        this.userRepository = userRepository;
    }
}
