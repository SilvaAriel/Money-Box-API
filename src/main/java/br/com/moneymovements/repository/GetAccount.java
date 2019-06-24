package br.com.moneymovements.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.moneymovements.domain.Account;

@Repository
public interface GetAccount{

	Optional<Account> findAccount(int account);
	List<Account> findAllAccounts();
}
