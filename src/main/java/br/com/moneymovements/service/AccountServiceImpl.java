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

import br.com.moneymovements.converter.DozerConverter;
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
import br.com.moneymovements.vo.MovementVO;
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
	public Account findAccount(int id) throws AccountNotFoundException{
		Optional<Account> accountEntity = this.accountRepository.findAccount(id);
		if (accountEntity.isPresent()) {
			return accountEntity.get();
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}

	}
	
	@Override
	public List<Account> findAllAccounts(){
		return this.accountRepository.findAllAccounts();
		
	}

	@Override
	public Account createAccount(String accname, double balance) throws OpenAccountException {
		try {
			Account accountCreated = accountManager.createAccount(accname, balance);
			this.accountRepository.save(accountCreated);
			return accountCreated;
		} catch (OpenAccountException e) {
			throw new OpenAccountException("Unable to open the account: " + accname);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to open the account: " + accname, e);
		}
	}

	@Override
	public boolean closeAccount(int id) throws CloseAccountException, AccountNotFoundException {
		boolean accExists = accountExists(id);
		Account accountEntity = findAccount(id);
		if (accExists) {
			try {
				Account accFromBase = accountManager.closeAccount(accountEntity);
				this.accountRepository.save(accountEntity);
				return true;
			} catch (CloseAccountException e) {
				throw new CloseAccountException("Unable to close the account: " + accountEntity.getName());
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to close the account: " + accountEntity.getName(), e);
			}
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}
	}

	public double getBalance(int id) throws CloseAccountException, AccountNotFoundException {
		boolean accExists = accountExists(id);
		Account accountEntity = findAccount(id);
		if (accExists) {
			return accountEntity.getBalance();
		} else {
			throw new CloseAccountException("Account not found or closed");
		}
	}
	
	public MovementVO deposit(MovementVO movement) throws UnableToDepositException, AccountNotFoundException {
		Optional<Account> accountExists = accountRepository.findAccount(movement.getAccount().getAccountId());
		
		Movement mov = DozerConverter.parseObject(movement, Movement.class);
		
		if (accountExists.isPresent() && accountExists.get().isStatus()) {
			try {
				mov.setDate(new Date());
				Account account = this.accountManager.depositCalc(accountExists.get(), mov);
				this.accountRepository.save(account);
				return DozerConverter.parseObject(this.movementRepository.save(mov), MovementVO.class);
			} catch (UnableToDepositException e) {
				throw new UnableToDepositException(
						"Unable to make a deposit to account: " + accountExists.get().getName());
			} catch (NullPointerException e) {
				throw new UnableToDepositException(
						"Unable to make a deposit to account");
			} 
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}
	}

	public Movement withdraw(Movement movement) throws InsufficientBalanceException, AccountNotFoundException {
		Optional<Account> accountExists = accountRepository.findAccount(movement.getAccount().getAccountId());
		
		if (accountExists.isPresent() && accountExists.get().isStatus()) {
			try {
				movement.setDate(new Date());
				Account newAccount = this.accountManager.withdrawCalc(accountExists.get(), movement);
				this.accountRepository.save(newAccount);
				this.movementRepository.save(movement);
				return movement;
			} catch (InsufficientBalanceException e) {
				throw new InsufficientBalanceException(
						"Insufficient balance on account: " + accountExists.get().getName());
			}
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}
	}

	public Movement transfer(Movement movement)
			throws InsufficientBalanceException, UnableToDepositException, AccountNotFoundException, SameAccountException {
		Account accountSource = findAccount(movement.getAccount().getAccountId());
		Account accountDestination = findAccount(movement.getDestAccountId());
		if (accountSource != null && accountDestination != null 
				&& accountSource.isStatus() && accountDestination.isStatus()) {
			if (accountSource != accountDestination) {
				try {
					movement.setDate(new Date());
					movement.setAccount(accountSource);
					this.accountRepository.save(this.accountManager.withdrawCalc(accountSource, movement));
					this.accountRepository.save(this.accountManager.depositCalc(accountDestination, movement));
					return this.movementRepository.save(movement);
				} catch (InsufficientBalanceException e) {
					throw new InsufficientBalanceException(
							"Insufficient balance on account: " + accountSource.getName());
				} catch (UnableToDepositException e) {
					throw new UnableToDepositException(
							"Unable to make a deposit to account: " + accountSource.getName());
				}
			} else {
				throw new SameAccountException("Unable to transfer to the same account");
			}
		} else {
			throw new AccountNotFoundException("Account not found or closed");
		}
		
	}

	public boolean accountExists(int id) {
		Optional<Account> accountExists = this.accountRepository.findById(id);
		if (accountExists.isPresent() && accountExists.get().isStatus()) {
			return true;
		}
		
		return false;

	}

}
