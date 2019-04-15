package br.com.moneymovements.domain;

import org.springframework.stereotype.Component;

import br.com.moneymovements.exception.InsufficientBalanceException;

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
	
	public Account withdrawCalc(Account acc, Movement mov) throws InsufficientBalanceException {
		double newValue = acc.getBalance() - mov.getValue();
		if (newValue < 0) {
			throw new InsufficientBalanceException("Insufficient balance");
		} else {
			acc.setBalance(newValue);
			return acc;
		}
	}
}
