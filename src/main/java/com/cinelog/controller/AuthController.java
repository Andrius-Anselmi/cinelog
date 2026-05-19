package com.cinelog.controller;


import com.cinelog.config.TokenService;
import com.cinelog.exception.UsernameOrPasswordInvalidException;
import com.cinelog.request.LoginRequest;
import com.cinelog.request.UserRequest;
import com.cinelog.response.LoginResponse;
import com.cinelog.response.UserResponse;
import com.cinelog.entity.User;
import com.cinelog.mapper.UserMapper;
import com.cinelog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cinelog/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest user) {
        return ResponseEntity.ok(UserMapper.toUserResponse(service.save(UserMapper.toUser(user))));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        try{
            UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            Authentication authentication = authenticationManager.authenticate(userAndPass);

            User user = (User) authentication.getPrincipal();

            String tokenUser = tokenService.generateToken(user);

            return ResponseEntity.ok(new LoginResponse(tokenUser));

        } catch (BadCredentialsException e){
            throw new UsernameOrPasswordInvalidException("User or password invalid");
        }




    }
}

