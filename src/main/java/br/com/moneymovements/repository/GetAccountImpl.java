package br.com.moneymovements.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.moneymovements.domain.Account;

@Repository
public class GetAccountImpl implements GetAccount {

	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public Optional<Account> findAccount(int account, Long userId){
		Query query = entityManager.createNativeQuery(String.format("select * from account where account_id = %s and user_id = %s", account, userId), Account.class);
		List<Account> accList = (List<Account>) query.getResultList();
		if (!accList.isEmpty()) {
			return Optional.of(accList.get(0));
		}
		return null;
	}

	@Override
	public List<Account> findAllAccounts(Long userId) {
		Query query = entityManager.createNativeQuery("select * from account where user_id = "+ userId, Account.class);
		return query.getResultList();
	}


}
