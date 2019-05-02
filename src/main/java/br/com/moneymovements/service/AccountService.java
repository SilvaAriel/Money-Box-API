package br.com.moneymovements.service;

import java.util.List;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.OpenAccountException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;

public interface AccountService {

	public List<Movement> getAllMovementsByAccount(int id);
	List<Movement> getAllMovementsByAccountSorted(int id, String sort);
	public Account createAccount(String accname, double balance) throws OpenAccountException;
	public boolean closeAccount(int id) throws CloseAccountException, AccountNotFoundException;
	public double getBalance(int id) throws CloseAccountException;
	public Movement deposit(Movement movement) throws UnableToDepositException, AccountNotFoundException;
	public Movement withdraw(Movement movement) throws InsufficientBalanceException, AccountNotFoundException;
	public Movement transfer(int accSource, int accDestination, Movement mov) throws InsufficientBalanceException, UnableToDepositException, AccountNotFoundException, SameAccountException;
	
}
