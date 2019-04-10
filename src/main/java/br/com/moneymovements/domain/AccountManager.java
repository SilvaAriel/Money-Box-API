package br.com.moneymovements.domain;

import org.springframework.stereotype.Component;

@Component
public class AccountManager  {
	
	public Account createAccount(String accname, double balance) {
		String accLower = accname.toLowerCase();
		Account acc = new Account(accLower, balance);
		return acc;
	}
	
	public Account closeAccount(Account acc) {
		acc.setStatus(false);
		return acc;
	}
	
	public Account depositCalc(Account acc, Movement mov) {
		double newValue = acc.getBalance() + mov.getValue();
		acc.setBalance(newValue);
		return acc;
	}
	
	public Account withdrawCalc(Account acc, Movement mov) {
		double newValue = acc.getBalance() - mov.getValue();
		acc.setBalance(newValue);
		return acc;
	}
		
}
