package com.jadavpur.durgamandir.controler;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.jadavpur.durgamandir.dto.AuthRequest;
import com.jadavpur.durgamandir.dto.ForgotPasswordRequest;
import com.jadavpur.durgamandir.security.util.JwtUtil;
import com.jadavpur.durgamandir.service.AuthService;

@RestController
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
		return ResponseEntity.ok(Map.of("token", jwt));
	}

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody AuthRequest authRequest) {
		if (authRequest.getUsername() == null || authRequest.getUsername().isBlank()
				|| authRequest.getPassword() == null || authRequest.getPassword().isBlank()) {
			return ResponseEntity.badRequest().body("Username and password must be provided");
		}

		String resultMessage = authService.signup(authRequest.getUsername(), authRequest.getEmail(), authRequest.getPassword());

		if (resultMessage.equals("User registered successfully")) {
			return ResponseEntity.ok(resultMessage);
		} else {
			return ResponseEntity.badRequest().body(resultMessage);
		}

	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgetPassword(@RequestBody ForgotPasswordRequest  forgotPasswordRequest ) {
		
		String resultMessage = authService.sendPasswordResetLink(forgotPasswordRequest.getEmail());

		if (resultMessage.equals("Password reset link sent successfully. Check your email for the for link and change the password.")) {
			return ResponseEntity.ok(resultMessage);
		} else {
			return ResponseEntity.badRequest().body(resultMessage);
		}
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
		String token = request.get("token");
		String newPassword = request.get("newPassword");

		if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
			return ResponseEntity.badRequest().body("Token and new password must be provided");
		}

		String resultMessage = authService.resetPassword(token, newPassword);

		if (resultMessage.equals("Password has been reset successfully.")) {
			return ResponseEntity.ok(resultMessage);
		} else {
			return ResponseEntity.badRequest().body(resultMessage);
		}
	}
}