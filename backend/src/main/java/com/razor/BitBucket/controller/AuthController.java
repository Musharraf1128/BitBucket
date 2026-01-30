package com.razor.BitBucket.controller;

import com.razor.BitBucket.dto.AuthResponse;
import com.razor.BitBucket.dto.LoginRequest;
import com.razor.BitBucket.dto.RegisterRequest;
import com.razor.BitBucket.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        System.out.println("regitered "+request);
        return new AuthResponse("User registered successfully");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        authService.login(request);
        return new AuthResponse("Login successful");
    }
}
