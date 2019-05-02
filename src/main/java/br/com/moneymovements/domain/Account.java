package br.com.moneymovements.domain;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private int id;
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private double balance;

	@JsonManagedReference
	@OneToMany(mappedBy = "account")
	@Basic(fetch=FetchType.LAZY)
	@Getter
	private Set<Movement> movements;

	@Getter
	@Setter
	private boolean status;

	public Account() {}
	
	public Account(String name, double balance) {
		this.name = name;
		this.balance = balance;
		this.status = true;
	}

}
