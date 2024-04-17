package com.iesvegademijas.serverSideSocialFlavours.controllers.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated.FilterRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filterapi")
public class FilterController {
    private final FilterRepository filterRepository;
    private final UserRepository userRepository;

    @Autowired
    public FilterController(FilterRepository filterRepository, UserRepository userRepository) {
        this.filterRepository = filterRepository;
        this.userRepository = userRepository;
    }


}
