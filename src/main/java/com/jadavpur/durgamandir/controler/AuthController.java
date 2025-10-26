package com.jadavpur.durgamandir.controler;

import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jadavpur.durgamandir.dto.AuthRequest;
import com.jadavpur.durgamandir.security.model.Authority;
import com.jadavpur.durgamandir.security.model.Users;
import com.jadavpur.durgamandir.security.repo.UserRepo;
import com.jadavpur.durgamandir.security.util.JwtUtil;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
        return ResponseEntity.ok(Map.of("token", jwt));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequest authRequest) {
        // Basic validation
        if (authRequest.getUsername() == null || authRequest.getUsername().isBlank() ||
            authRequest.getPassword() == null || authRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Username and password must be provided");
        }

        // Check if user already exists
        if (userRepo.findByUsername(authRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body("User already exists");
        }

        // Encode password and create user with ROLE_USER
        String encoded = passwordEncoder.encode(authRequest.getPassword());
        Users user = new Users();
        user.setUsername(authRequest.getUsername());
        user.setPassword(encoded);

        Authority auth = new Authority();
        auth.setUsername(authRequest.getUsername());
        auth.setAuthority("ROLE_USER");

        Set<Authority> auths = new HashSet<>();
        auths.add(auth);
        user.setAuthorities(auths);

        userRepo.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
    
    
}