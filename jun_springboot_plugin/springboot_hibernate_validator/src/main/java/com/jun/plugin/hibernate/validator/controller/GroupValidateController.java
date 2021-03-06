package com.jun.plugin.hibernate.validator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jun.plugin.hibernate.validator.dto.User;

@RestController
@RequestMapping("/group")
public class GroupValidateController {

    @PostMapping("/user")
    public ResponseEntity<String> save(@Validated(value = {User.OnCreate.class}) @RequestBody User user) {
        return ResponseEntity.ok("valid");
    }
}
