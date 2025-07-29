package com.atk.userservice.controller;


import com.atk.userservice.dto.LoginRequestDto;
import com.atk.userservice.dto.LoginResponseDto;
import com.atk.userservice.dto.RegisterRequestDto;
import com.atk.userservice.entity.UserInformation;
import com.atk.userservice.repository.UserInformationRepository;
import com.atk.userservice.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final UserInformationRepository repository;
    private final JwtUtil util;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        UserInformation user = UserInformation.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password()) // buraya password encoder eklenmeli
                .build();
        repository.save(user);
        return ResponseEntity.ok("✅ Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        UserInformation user = repository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = util.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

}
