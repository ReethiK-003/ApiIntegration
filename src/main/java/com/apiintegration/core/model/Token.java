package com.apiintegration.core.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	protected String data;

	protected String type;

	protected String token;

	@ToString.Exclude
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	protected User user;

	@CreationTimestamp
	protected Timestamp createdAt;

	protected Date expiresAt;

	@Version
	protected Long version;

	public boolean hasExpired() {
		return expiresAt.before(new Date());
	}

	public void expireNow() {
		this.expiresAt = new Date(System.currentTimeMillis() - 1000);
	}

	public static Date calculateExpiryDate(int expiryTimeInMinutes) {
		return calculateExpiryDate(expiryTimeInMinutes, false);
	}

	public static Date calculateExpiryDate(int expiryTimeInMinutes, boolean negativeTime) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));

		if (negativeTime) {
			cal.add(Calendar.MINUTE, -expiryTimeInMinutes);
		} else {
			cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		}
		return new Date(cal.getTime().getTime());
	}
}
