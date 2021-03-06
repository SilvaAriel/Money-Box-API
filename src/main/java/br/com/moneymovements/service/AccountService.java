package br.com.moneymovements.service;

import java.util.List;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.OpenAccountException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.vo.AccountVO;
import br.com.moneymovements.vo.MovementVO;

public interface AccountService {

	public List<MovementVO> getAllMovementsByAccount(int id);
	public List<MovementVO> getAllMovementsByAccountSorted(int id, String sort);
	public Account findAccount(int id) throws AccountNotFoundException;
	public List<AccountVO> findAllAccounts();
	public AccountVO createAccount(String accname, double balance) throws OpenAccountException;
	public boolean closeAccount(int id) throws CloseAccountException, AccountNotFoundException;
	public double getBalance(int id) throws CloseAccountException, AccountNotFoundException;
	public MovementVO deposit(MovementVO movement) throws UnableToDepositException, AccountNotFoundException;
	public MovementVO withdraw(MovementVO movement) throws InsufficientBalanceException, AccountNotFoundException;
	public MovementVO transfer(MovementVO movement) throws InsufficientBalanceException, UnableToDepositException, AccountNotFoundException, SameAccountException;
}
