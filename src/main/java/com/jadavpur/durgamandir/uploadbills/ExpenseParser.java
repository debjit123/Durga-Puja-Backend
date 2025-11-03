package com.jadavpur.durgamandir.uploadbills;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.jadavpur.durgamandir.model.Expense;

@Service
public class ExpenseParser {

	public List<Expense> parseToExpenseObject(String text, String expenseType, String userName) {

		List<Expense> expenses = new ArrayList<>();
		Expense expense = null;
		String expenseItem = "";
		double amount = 0.0;

		Pattern p = Pattern.compile("\\s+(\\d+)\\s+(\\w+)+");
		Matcher m = p.matcher(text);
		while (m.find()) {
			expense = new Expense();
			amount = Double.parseDouble(m.group(1).trim());
			expenseItem = m.group(2).trim();

			expense.setExpenseItem(expenseItem);
			expense.setExpenseType(expenseType);
			expense.setExpenseAmount(amount);
			expense.setPurchaseDate(new Date());
			expense.setCreateUser(userName);
			expenses.add(expense);

		}
		return expenses;

	}

}
