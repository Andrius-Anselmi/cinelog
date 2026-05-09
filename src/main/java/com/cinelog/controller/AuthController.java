package com.cinelog.controller;

import com.cinelog.controller.request.UserRequest;
import com.cinelog.controller.response.UserResponse;
import com.cinelog.mapper.UserMapper;
import com.cinelog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cinelog/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest user){
        return ResponseEntity.ok(UserMapper.toUserResponse(service.save(UserMapper.toUser(user))));
    }
}
