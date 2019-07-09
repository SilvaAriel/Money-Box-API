package br.com.moneymovements.vo;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.hateoas.ResourceSupport;

import lombok.Getter;
import lombok.Setter;

public class MovementVO extends ResourceSupport {
	
	@Getter @Setter private int movementId;
	@Getter @Setter private Date date;
	@Getter @Setter private String detail;
	@Getter @Setter private float value;
	@Getter @Setter	private int destAccountId;
	
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
	
	
	
}
