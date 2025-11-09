package com.jadavpur.durgamandir.service.imp;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jadavpur.durgamandir.security.model.Authority;
import com.jadavpur.durgamandir.security.model.Users;
import com.jadavpur.durgamandir.security.repo.UserRepo;
import com.jadavpur.durgamandir.security.util.JwtUtil;
import com.jadavpur.durgamandir.service.AuthService;
import com.jadavpur.durgamandir.service.EmailService;

@Component
public class AuthServiceImp implements AuthService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private EmailService emailService;

	@Override
	public String signup(String username, String email, String password) {

		if (userRepo.findByUsername(username).isPresent()) {
			return "User already exists";
		}

		try {
			String encodedPassword = passwordEncoder.encode(password);
			Users user = new Users();
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(encodedPassword);

			Authority auth = new Authority();
			auth.setUsername(username);
			auth.setAuthority("ROLE_USER");

			Set<Authority> auths = new HashSet<>();
			auths.add(auth);
			user.setAuthorities(auths);

			userRepo.save(user);
		} catch (Exception e) {

			Logger.getLogger(AuthServiceImp.class.getName())
					.severe("Error during user registration: " + e.getMessage());

			return "Error during registration: " + e.getMessage();
		}

		return "User registered successfully";
	}

	@Override
	public String sendPasswordResetLink(String email) {

		try {
			
			if (userRepo.findByEmail(email).isEmpty()) {
				return "User does not exists";
			}
			
			String token = jwtUtil.generateResetToken(email);
			String link = "http://localhost:3000/reset-password?token=" + token;
			System.out.println("Password reset link: " + link);
			emailService.sendEmail(email, "Password Reset Request", "Click the link to reset your password: " + link);
		} catch (Exception e) {
			Logger.getLogger(AuthServiceImp.class.getName()).severe("Error during password reset: " + e.getMessage());
			return "Error during password reset: " + e.getMessage();

		}

		return "Password reset link sent successfully. Check your email for the for link and change the password.";
	}

	@Override
	public String resetPassword(String token, String newPassword) {
		
		
		String email;
		try {
			email = jwtUtil.getEmailFromToken(token);
			System.out.println("Token: " + token);
			Users user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepo.save(user);
		} catch (RuntimeException e) {
			
			Logger.getLogger(AuthServiceImp.class.getName())
					.severe("Error during password reset: " + e.getMessage());
			return "Error during password reset: " + e.getMessage();
		}
		
		return "Password has been reset successfully.";

	}

}
