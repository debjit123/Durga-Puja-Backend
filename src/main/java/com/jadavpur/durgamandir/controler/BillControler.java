package com.jadavpur.durgamandir.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jadavpur.durgamandir.service.BillUploadService;

@RestController
public class BillControler {

	private BillUploadService billUploadService;

	@Autowired
	public BillControler(BillUploadService billUploadService) {
		this.billUploadService = billUploadService;
	}

	@PostMapping("/uploadBill")
	public ResponseEntity<String> uploadBill(@RequestParam("file") MultipartFile file,
			@RequestParam("expenseType") String expenseType) {
		try {
			billUploadService.uploadBillAndSaveExpense(file, expenseType);
			return ResponseEntity.ok("Expense added successfully! and URL is");
		} catch (Exception e) {
			System.err.println("Error during file upload: " + e.getMessage());
			return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
		}
	}

}
