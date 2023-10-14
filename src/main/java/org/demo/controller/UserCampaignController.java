package org.demo.controller;

import lombok.RequiredArgsConstructor;
import org.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserCampaignController {
    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<HttpStatus> postAttributeForUser(
            @RequestParam("userId") int userId,
            @RequestParam("attributeId") int attributeId) {

        return userService.addUserWithAttribute(userId, attributeId) ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();

    }

    @GetMapping("/campaign")
    public int getCampaignForUser(@RequestParam("userId") int userId) {
        return userService.getCampaignForUser(userId);

    }


}
