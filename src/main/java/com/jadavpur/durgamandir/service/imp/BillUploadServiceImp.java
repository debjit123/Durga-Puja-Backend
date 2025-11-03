package com.jadavpur.durgamandir.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.jadavpur.durgamandir.dao.ExpenseDaoImp;
import com.jadavpur.durgamandir.model.Expense;
import com.jadavpur.durgamandir.service.BillUploadService;
import com.jadavpur.durgamandir.service.FileExtractionService;
import com.jadavpur.durgamandir.service.StorageService;
import com.jadavpur.durgamandir.uploadbills.ExpenseParser;

@Component
public class BillUploadServiceImp implements BillUploadService {
	
	private StorageService storageService;
	private FileExtractionService fileExtractionService;
	private ExpenseParser expenseParser;
	private ExpenseDaoImp expenseDaoImp;

	@Autowired
	public BillUploadServiceImp(@Qualifier("localStorageService") StorageService storageService,
			FileExtractionService fileExtractionService, ExpenseParser expenseParser, ExpenseDaoImp expenseDaoImp) {
		this.expenseParser = expenseParser;
		this.storageService = storageService;
		this.fileExtractionService = fileExtractionService;
		this.expenseDaoImp = expenseDaoImp;
	}

	@Override
	public void uploadBillAndSaveExpense(MultipartFile file, String expenseType) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		storageService.store(file);
		
		String extractedText = fileExtractionService.extractTextFromFile(file);
		List<Expense> expenses = expenseParser.parseToExpenseObject(extractedText, expenseType, username);
		expenseDaoImp.addMultipleExpenses(expenses);
	}

}
