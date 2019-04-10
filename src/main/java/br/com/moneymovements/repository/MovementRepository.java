package br.com.moneymovements.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;

@Repository
public interface MovementRepository extends CrudRepository <Movement, Integer>, GetAccountMovements {
	
	<S extends Movement> S save (Movement movement);

}
