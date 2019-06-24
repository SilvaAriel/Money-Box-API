package br.com.moneymovements.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.moneymovements.domain.Account;

@Repository
public interface GetAccount{

	Account findAccount(int account);
}
