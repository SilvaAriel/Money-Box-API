package br.com.moneymovements.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.AccountManager;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.repository.AccountRepository;
import br.com.moneymovements.repository.MovementRepository;
import lombok.Getter;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountManager accountManager;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private MovementRepository movementRepository;
	
	@Getter private Set<Account> accounts = new HashSet<>();
	
	@Override
	public List<Movement> getAllMovementsByAccount(int id) {
		return this.movementRepository.getAccountMovements(id);
	}
	
	@Override
	public List<Movement> getAllMovementsByAccountSorted(int id, String sort) {
		return this.movementRepository.getAccountMovementsSorted(id, sort);
	}

	@Override
	public Account createAccount(String accname, double balance) {
		Account acc = accountManager.createAccount(accname, balance);
		this.accountRepository.save(acc);
		return acc;
	}

	@Override
	public boolean closeAccount(int id) {
		Account accFromBase = this.accountRepository.findById(id).orElse(null);
		Account accClosed = accountManager.closeAccount(accFromBase);
		this.accountRepository.save(accClosed);
		return true;
	}
	
	public double getBalance(int id) {
		Account accFromBase = this.accountRepository.findById(id).orElse(null);
		return accFromBase.getBalance();
	}
	
	public Movement deposit(Movement movement) {
		movement.setDate(new Date());
		Account newAccount = this.accountManager.depositCalc(movement.getAccount(), movement);
		this.accountRepository.save(newAccount);
		return this.movementRepository.save(movement);
	}
	
	public Movement withdraw(Movement movement) {
		movement.setDate(new Date());
		Account newAccount = null;
		try {
			newAccount = this.accountManager.withdrawCalc(movement.getAccount(), movement);
			this.accountRepository.save(newAccount);
			this.movementRepository.save(movement);
		} catch (InsufficientBalanceException e) {
			e.getMessage();
		}
		return movement;
	}
	
	public Movement transfer(int accSource, int accDestination, Movement movement) {
		movement.setDate(new Date());
		Account source = this.accountRepository.findById(accSource).orElse(null);
		Account destinarion = this.accountRepository.findById(accDestination).orElse(null);
		try {
			this.accountRepository.save(this.accountManager.withdrawCalc(source, movement));
		} catch (InsufficientBalanceException e) {
			e.getMessage();
		}
		this.accountRepository.save(this.accountManager.depositCalc(destinarion, movement));
		return this.movementRepository.save(movement);
	}

}
