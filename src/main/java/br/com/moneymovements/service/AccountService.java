package br.com.moneymovements.service;

import java.util.List;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;

public interface AccountService {

	public List<Movement> getAllMovementsByAccount(int id);
	List<Movement> getAllMovementsByAccountSorted(int id, String sort);
	public Account createAccount(String accname, double balance);
	public boolean closeAccount(int id);
	public double getBalance(int id);
	public Movement deposit(Movement movement);
	public Movement withdraw(Movement movement);
	public Movement transfer(int accSource, int accDestination, Movement mov);
	
}
