package br.com.moneymovements.vo;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import br.com.moneymovements.domain.Account;

public class MovementVO extends ResourceSupport {
	
	private int movementId;
	private Date date;
	private String detail;
	private float value;
	private Account account;
	private int destAccountId;
	
	public MovementVO() {}
	
	public MovementVO(String detail, float value) {
		this.detail = detail;
		this.value = value;
		this.date = new Date();
	}
	
	public MovementVO(MovementVO movement) {
		this.detail = movement.getDetail();
		this.value = movement.getValue();
	}

	public int getMovementId() {
		return movementId;
	}

	public void setMovementId(int movementId) {
		this.movementId = movementId;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public int getDestAccountId() {
		return destAccountId;
	}

	public void setDestAccountId(int destAccountId) {
		this.destAccountId = destAccountId;
	}
	
	
}
