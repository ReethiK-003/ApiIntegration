package com.apiintegration.core.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class UserVisits {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	private String deviceName;

	private String ipAddress;

	@CreationTimestamp
	private Timestamp timestamp;

	public UserVisits(String ipAddress, String deviceName) {
		this.ipAddress = ipAddress;
		this.deviceName = deviceName;
	}
}