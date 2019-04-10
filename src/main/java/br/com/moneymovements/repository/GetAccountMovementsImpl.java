package br.com.moneymovements.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.moneymovements.domain.Movement;

@Repository
public class GetAccountMovementsImpl implements GetAccountMovements {

	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public List<Movement> getAccountMovements(int id) {
		Query query = entityManager.createNativeQuery(String.format("select * from movement where account_id LIKE %s", id), Movement.class);
		return query.getResultList();
	}

	@Override
	public List<Movement> getAccountMovementsSorted(int id, String sort) {
		
		Query query = null;
		if (sort.equals("date") || sort.equals("value")) {
			query = entityManager.createNativeQuery(String.format("select * from movement where account_id LIKE %s order by %s", id, sort), Movement.class);
		} else {
			return null;
		}
		return query.getResultList();
	}

}
