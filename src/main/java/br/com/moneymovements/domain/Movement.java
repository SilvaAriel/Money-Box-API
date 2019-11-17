package br.com.moneymovements.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="movement")
public class Movement extends ResourceSupport {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int movementId;
	private Date date;
	private String detail;
	private float value;
	@ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
	@JsonBackReference
	private Account account;
	private int destAccountId;
	
	public Movement() {}
	
	public Movement(String detail, float value) {
		this.detail = detail;
		this.value = value;
		this.date = new Date();
	}
	
	public Movement(Movement movement) {
		this.detail = movement.getDetail();
		this.value = movement.getValue();
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public int getDestAccountId() {
		return destAccountId;
	}

	public void setDestAccountId(int destAccountId) {
		this.destAccountId = destAccountId;
	}

	public int getMovementId() {
		return movementId;
	}
	
}
