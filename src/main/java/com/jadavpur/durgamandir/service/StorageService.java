package com.jadavpur.durgamandir.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface StorageService {
	 String store(MultipartFile file);
}
