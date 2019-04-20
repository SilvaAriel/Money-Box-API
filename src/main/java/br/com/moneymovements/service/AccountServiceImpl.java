package br.com.moneymovements.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.AccountManager;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.OpenAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.exception.CloseAccountException;
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
		try {
			Account acc;
			acc = accountManager.createAccount(accname, balance);
			this.accountRepository.save(acc);
			return acc;
		} catch (OpenAccountException e) {
			throw new ResponseStatusException (HttpStatus.INTERNAL_SERVER_ERROR, "Unable to open the account: " + accname, e);
		}
	}
	//TESTAR---------------------------------------
	//GENERALIZAR AS EXCEPTIONS
	@Override
	public boolean closeAccount(int id) {
		//Account accFromBase = this.accountRepository.findById(id).orElse(null);
		Account accFromBase = null;
		try {
			accFromBase = accountManager.closeAccount(accFromBase);
			this.accountRepository.save(accFromBase);
			return true;
		} catch (CloseAccountException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to close the account: " + accFromBase.getName(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to close the account: " + accFromBase.getName(), e);
		}
	}
	//TESTAR---------------------------------------
	public double getBalance(int id) {
		Account accFromBase = this.accountRepository.findById(id).orElse(null);
		return accFromBase.getBalance();
	}
	//TESTAR---------------------------------------
	public Movement deposit(Movement movement) {
		try {
			movement.setDate(new Date());
			Account account;
			account = this.accountManager.depositCalc(movement.getAccount(), movement);
			this.accountRepository.save(account);
			return this.movementRepository.save(movement);
		} catch (UnableToDepositException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to make a deposit to account: " + movement.getAccount().getName(), e);
		}
	}
	//TESTAR---------------------------------------
	public Movement withdraw(Movement movement) {
		try {
			movement.setDate(new Date());
			Account newAccount;
			newAccount = this.accountManager.withdrawCalc(movement.getAccount(), movement);
			this.accountRepository.save(newAccount);
			this.movementRepository.save(movement);
			return movement;
		} catch (InsufficientBalanceException e) {
			throw new ResponseStatusException (HttpStatus.CONFLICT, "Insufficient balance on account: " + movement.getAccount().getName(), e);
		}
	}
	//TESTAR---------------------------------------
	public Movement transfer(int accSource, int accDestination, Movement movement) {
		Account source = this.accountRepository.findById(accSource).orElse(null);
		Account destinarion = this.accountRepository.findById(accDestination).orElse(null);
		try {
			movement.setDate(new Date());
			this.accountRepository.save(this.accountManager.withdrawCalc(source, movement));
			this.accountRepository.save(this.accountManager.depositCalc(destinarion, movement));
			return this.movementRepository.save(movement);
		} catch (InsufficientBalanceException e) {
			throw new ResponseStatusException (HttpStatus.CONFLICT, "Insufficient balance on account: " + source.getName(), e);
		} catch (UnableToDepositException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to make a deposit to account: " + movement.getAccount().getName(), e);

		}
	}

}
