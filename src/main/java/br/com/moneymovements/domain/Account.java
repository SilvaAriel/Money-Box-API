package br.com.moneymovements.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "account")
public class Account extends ResourceSupport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int accountId;
	private String name;
	private double balance;
	@JsonManagedReference @OneToMany(mappedBy = "account") @Basic(fetch=FetchType.LAZY)
	private List<Movement> movements;
	private boolean status;

	public Account() {}
	
	public Account(String account) {}
	
	public Account(String name, double balance) {
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

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<Movement> getMovements() {
		return movements;
	}
	
	
}
