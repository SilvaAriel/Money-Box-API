package br.com.moneymovements.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.moneymovements.domain.Account;

@Repository
public interface AccountRepository extends CrudRepository <Account, Integer>, GetAccount {

	Set<Account> findAll();
	Optional<Account> findById(Integer id);
}
