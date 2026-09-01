package com.example.Booking.Controller;

import com.example.Booking.Dto.LoginRequestDto;
import com.example.Booking.Dto.LoginResponseDto;
import com.example.Booking.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}