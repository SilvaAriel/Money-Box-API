package br.com.moneymovements.repository;

import java.util.List;

import br.com.moneymovements.domain.Movement;

public interface GetAccountMovements {
	
	List<Movement> getAccountMovements(int id);
	List<Movement> getAccountMovementsSorted(int id, String sort);

}
