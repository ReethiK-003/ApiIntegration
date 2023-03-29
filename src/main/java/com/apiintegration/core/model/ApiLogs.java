package com.apiintegration.core.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.apiintegration.core.model.service.ObjectToDbJsonConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class ApiLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "api_id")
	private Api api;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "request_data", columnDefinition = "json")
	@Convert(converter = ObjectToDbJsonConverter.class)
	private Object requestData;

	@Column(name = "response_data", columnDefinition = "json")
	@Convert(converter = ObjectToDbJsonConverter.class)
	private Object responseData;

	private int status;

	@CreationTimestamp
	private Timestamp committedAt;

}