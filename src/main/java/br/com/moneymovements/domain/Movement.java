package br.com.moneymovements.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="movement")
public class Movement {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Getter private int id;
	@Temporal(TemporalType.TIME)
	@Getter @Setter private Date date;
	@Getter @Setter private String detail;
	@Getter @Setter private float value;
	@ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
	@JsonBackReference
	@Getter @Setter private Account account;
	
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
	
}
