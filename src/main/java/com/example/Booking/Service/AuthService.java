package com.example.Booking.Service;

import com.example.Booking.Dto.LoginRequestDto;
import com.example.Booking.Dto.LoginResponseDto;
import com.example.Booking.Entity.User;
import com.example.Booking.Security.JwtService;
import com.example.Booking.Security.Userdetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponseDto login(LoginRequestDto request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        if (!(authentication.getPrincipal() instanceof Userdetails principal)) {
            throw new UsernameNotFoundException("Authenticated user principal is unavailable");
        }
        String token = jwtService.generateToken(principal.getUsers());

        return new LoginResponseDto(token);
    }
}
