package com.jadavpur.durgamandir.service.imp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.jadavpur.durgamandir.service.StorageService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3StorageService implements StorageService {

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	private final S3Client s3Client;

	public S3StorageService(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	@Override
	public String store(MultipartFile file) {
		try {
			String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName).build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
		} catch (Exception e) {
			throw new RuntimeException("Failed to upload file to S3", e);
		}
	}
}
