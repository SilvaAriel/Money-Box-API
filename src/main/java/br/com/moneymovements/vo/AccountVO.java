package br.com.moneymovements.vo;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.hateoas.ResourceSupport;

import br.com.moneymovements.domain.Movement;
import lombok.Getter;
import lombok.Setter;

public class AccountVO extends ResourceSupport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private int accountId;
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private double balance;

	@Getter
	private List<Movement> movements;

	@Getter
	@Setter
	private boolean status;

	public AccountVO() {}
	
	public AccountVO(String account) {}
	
	public AccountVO(String name, double balance) {
		this.name = name;
		this.balance = balance;
		this.status = true;
	}
	
}
