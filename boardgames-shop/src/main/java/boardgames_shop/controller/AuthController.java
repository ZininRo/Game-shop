// src/main/java/boardgames_shop/controller/AuthController.java
package boardgames_shop.controller;

import boardgames_shop.dto.auth.LoginRequest;
import boardgames_shop.dto.auth.LoginResponse;
import boardgames_shop.dto.auth.RegisterRequest;
import boardgames_shop.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {  // FIX: убран @CrossOrigin (дублировал глобальный CORS)

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}