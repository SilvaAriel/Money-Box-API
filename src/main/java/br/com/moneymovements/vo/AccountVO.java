package br.com.moneymovements.vo;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.hateoas.ResourceSupport;

public class AccountVO extends ResourceSupport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int accountId;
	private String name;
	private double balance;
	private List<MovementVO> movements;
	private boolean status;

	public AccountVO() {}
	
	public AccountVO(String account) {}
	
	public AccountVO(String name, double balance) {
		this.name = name;
		this.balance = balance;
		this.status = true;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public List<MovementVO> getMovements() {
		return movements;
	}

	public void setMovements(List<MovementVO> movements) {
		this.movements = movements;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
}
