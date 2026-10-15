package com.example.tp2.controller;

import com.example.tp2.dto.AuthResponse;
import com.example.tp2.dto.LoginRequest;
import com.example.tp2.dto.RegisterRequest;
import com.example.tp2.entity.AppUser;
import com.example.tp2.repository.UserRepository;
import com.example.tp2.service.CustomerUserDetailsService;
import com.example.tp2.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerUserDetailsService userDetailsService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomerUserDetailsService userDetailsService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }

        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");

        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur créé");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.email());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }
}