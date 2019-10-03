package br.com.moneymovements.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.moneymovements.domain.User;

@Repository
public interface UserRepository extends CrudRepository <User, Integer> {

	@Query("SELECT u FROM User u WHERE u.username = :username")
	User findByUserName(@Param("username") String username);
	
	
	
}
