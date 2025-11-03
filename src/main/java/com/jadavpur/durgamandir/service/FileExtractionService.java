package com.jadavpur.durgamandir.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileExtractionService {
	
		String extractTextFromFile(MultipartFile file);
		
		String extractTextFromImage(MultipartFile file);
		
		

}
