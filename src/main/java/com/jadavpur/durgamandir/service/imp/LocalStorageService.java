package com.jadavpur.durgamandir.service.imp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.jadavpur.durgamandir.service.StorageService;

@Component
public class LocalStorageService implements StorageService {

	@Value("${app.storage.local.path}")
	private String uploadDir;

	@Override
	public String store(MultipartFile file) {
		try {
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
			Path filePath = uploadPath.resolve(fileName);

			Files.copy(file.getInputStream(), filePath);

			return filePath.toString(); // return local file path
		} catch (Exception e) {
			throw new RuntimeException("File upload failed", e);
		}
	}
}
