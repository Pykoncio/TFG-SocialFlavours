package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated.planner;

import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.planner.PlannerEntryRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plannerentryapi")
public class PlannerEntryController {
    private final PlannerEntryRepository plannerEntryRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlannerEntryController(PlannerEntryRepository plannerEntryRepository, UserRepository userRepository) {
        this.plannerEntryRepository = plannerEntryRepository;
        this.userRepository = userRepository;
    }
}
