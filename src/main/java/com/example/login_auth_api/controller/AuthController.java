package com.example.login_auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.login_auth_api.domain.User;
import com.example.login_auth_api.dto.LoginRequestDTO;
import com.example.login_auth_api.dto.RegisterRequestDTO;
import com.example.login_auth_api.dto.ResponseDTO;
import com.example.login_auth_api.infra.security.TokenService;
import com.example.login_auth_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    
    @PostMapping("/login")
    public  ResponseEntity login(@RequestBody LoginRequestDTO loginRequestDTO) {
        User user = this.userRepository.findByEmail(loginRequestDTO.email()).orElseThrow(() -> new RuntimeException("User not Found"));
       
        if (passwordEncoder.matches(loginRequestDTO.password(),user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }
    
    @PostMapping("/register")
    public  ResponseEntity register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        var present = this.userRepository.findByEmail(registerRequestDTO.email());
        
        if(present.isEmpty()){
            User user = new User();
            user.setPassword(passwordEncoder.encode(registerRequestDTO.password()));    
            user.setName(registerRequestDTO.name());
            user.setEmail(registerRequestDTO.email());
            this.userRepository.save(user);

            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }

        return ResponseEntity.badRequest().build();    
      
    }
    
}
