
package com.esign.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.esign.auth.dto.AuthResponse;
import com.esign.auth.dto.LoginRequest;
import com.esign.auth.dto.RegisterRequest;
import com.esign.auth.entity.User;
import com.esign.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // USER REGISTRATION
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        // Generate JWT Token
        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return new AuthResponse(
                "Registration successful",
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // USER LOGIN
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                    new RuntimeException("User not found!")
                );

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new RuntimeException("Invalid password!");
        }

        // Generate JWT Token
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(
                "Login successful",
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
