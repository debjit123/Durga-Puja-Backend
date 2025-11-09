package com.jadavpur.durgamandir.service;

import org.springframework.stereotype.Service;

@Service
public interface AuthService {
	
	 String signup(String username, String email,String password);

	 String sendPasswordResetLink(String email);

	 String resetPassword(String token, String newPassword);

}
