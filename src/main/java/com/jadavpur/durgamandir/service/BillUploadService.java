package com.jadavpur.durgamandir.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface BillUploadService {
	
	 void uploadBillAndSaveExpense(MultipartFile filePath, String expenseType);

}
