package br.com.moneymovements.vo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.hateoas.ResourceSupport;

import lombok.Getter;
import lombok.Setter;

public class AccountVO extends ResourceSupport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter	private int accountId;
	@Getter @Setter	private String name;
	@Getter @Setter	private double balance;
	//@Setter @Getter	private List<MovementVO> movements;
	//@Getter @Setter private boolean status;
	//@Getter	@Setter private UserVO user;

	public AccountVO() {}
	
	public AccountVO(String account) {}
	
	public AccountVO(String name, double balance) {
		this.name = name;
		this.balance = balance;
	}
	
}
