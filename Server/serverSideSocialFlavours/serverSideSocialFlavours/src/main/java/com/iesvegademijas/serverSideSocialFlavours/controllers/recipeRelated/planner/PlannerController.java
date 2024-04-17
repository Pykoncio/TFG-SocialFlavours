package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated.planner;

import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.planner.PlannerRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plannerapi")
public class PlannerController {
    private final PlannerRepository plannerRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlannerController(PlannerRepository plannerRepository, UserRepository userRepository) {
        this.plannerRepository = plannerRepository;
        this.userRepository = userRepository;
    }
}
