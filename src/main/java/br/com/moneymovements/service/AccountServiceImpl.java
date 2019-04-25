package br.com.moneymovements.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.AccountManager;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.OpenAccountException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
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

	@Getter
	private Set<Account> accounts = new HashSet<>();

	@Override
	public List<Movement> getAllMovementsByAccount(int id) {
		return this.movementRepository.getAccountMovements(id);
	}

	@Override
	public List<Movement> getAllMovementsByAccountSorted(int id, String sort) {
		return this.movementRepository.getAccountMovementsSorted(id, sort);
	}

	@Override
	public Account createAccount(String accname, double balance) throws OpenAccountException {
		try {
			Account acc;
			acc = accountManager.createAccount(accname, balance);
			this.accountRepository.save(acc);
			return acc;
		} catch (OpenAccountException e) {
			throw new OpenAccountException("Unable to open the account: " + accname);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to open the account: " + accname, e);
		}
	}

	@Override
	public boolean closeAccount(int id) throws CloseAccountException, AccountNotFoundException {
		Account accExists = accountExists(id);
		if (accExists != null) {
			Account accFromBase = accExists;
			try {
				accFromBase = accountManager.closeAccount(accFromBase);
				this.accountRepository.save(accFromBase);
				return true;
			} catch (CloseAccountException e) {
				throw new CloseAccountException("Unable to close the account: " + accFromBase.getName());
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to close the account: " + accFromBase.getName(), e);
			}
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}
	}

	public double getBalance(int id) throws CloseAccountException {
		Account accExists = accountExists(id);
		if (accExists != null) {
			return accExists.getBalance();
		} else {
			throw new CloseAccountException("Account not found or closed");
		}
	}

	public Movement deposit(Movement movement) throws UnableToDepositException, AccountNotFoundException {
		Account accExists = null;
		if (movement.getAccount() != null) {
			accExists = accountExists(movement.getAccount().getId());
		}
		if (accExists != null) {
			try {
				movement.setDate(new Date());
				Account account;
				account = this.accountManager.depositCalc(movement.getAccount(), movement);
				this.accountRepository.save(account);
				return this.movementRepository.save(movement);
			} catch (UnableToDepositException e) {
				throw new UnableToDepositException(
						"Unable to make a deposit to account: " + movement.getAccount().getName());
			}
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}
	}

	public Movement withdraw(Movement movement) throws InsufficientBalanceException, AccountNotFoundException {
		Account accExists = null;
		if (movement.getAccount() != null) {
			accExists = accountExists(movement.getAccount().getId());
		}
		if (accExists != null) {
			try {
				movement.setDate(new Date());
				Account newAccount;
				newAccount = this.accountManager.withdrawCalc(movement.getAccount(), movement);
				this.accountRepository.save(newAccount);
				this.movementRepository.save(movement);
				return movement;
			} catch (InsufficientBalanceException e) {
				throw new InsufficientBalanceException(
						"Insufficient balance on account: " + movement.getAccount().getName());
			}
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}
	}

	public Movement transfer(int accSource, int accDestination, Movement movement)
			throws InsufficientBalanceException, UnableToDepositException, AccountNotFoundException, SameAccountException {
		Account source = accountExists(accSource);
		Account destination = accountExists(accDestination);
		if (source != null && destination != null) {
			if (source != destination) {
				try {
					movement.setDate(new Date());
					this.accountRepository.save(this.accountManager.withdrawCalc(source, movement));
					this.accountRepository.save(this.accountManager.depositCalc(destination, movement));
					return this.movementRepository.save(movement);
				} catch (InsufficientBalanceException e) {
					throw new InsufficientBalanceException(
							"Insufficient balance on account: " + movement.getAccount().getName());
				} catch (UnableToDepositException e) {
					throw new UnableToDepositException(
							"Unable to make a deposit to account: " + movement.getAccount().getName());
				}
			} else {
				throw new SameAccountException("Unable to transfer to the same account");
			}
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}

	}

	public Account accountExists(int id) {
		Optional<Account> accExists = this.accountRepository.findById(id);
		if (accExists.isPresent() && accExists.get().isStatus()) {
			return accExists.get();
		}
		return null;

	}

}
