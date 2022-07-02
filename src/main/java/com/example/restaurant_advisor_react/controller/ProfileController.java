package com.example.restaurant_advisor_react.controller;

import com.example.restaurant_advisor_react.AuthUser;
import com.example.restaurant_advisor_react.model.User;
import com.example.restaurant_advisor_react.servise.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static com.example.restaurant_advisor_react.util.validation.ValidationUtil.assureIdConsistent;
import static com.example.restaurant_advisor_react.util.validation.ValidationUtil.checkModificationAllowed;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
@RestController
@RequestMapping(path = ProfileController.REST_URL, produces = APPLICATION_JSON_VALUE)
public class ProfileController {
    static final String REST_URL = "/api/v1/profile";

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<User> get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get profile {}", authUser.id());
        final Optional<User> user = Optional.of(authUser.getUser());
        return ResponseEntity.ok(user.get());
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> update(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody User user) {
        log.info("update {} with id={}", user, authUser.id());
        assureIdConsistent(user, authUser.id());
        checkModificationAllowed(authUser.id());
        Optional<User> updatedUser = userService.updateUser(user, authUser.id());
        if (updatedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser.get());
    }
}