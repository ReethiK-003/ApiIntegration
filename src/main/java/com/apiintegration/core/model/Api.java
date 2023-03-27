package com.apiintegration.core.model;

import java.sql.Timestamp;
import java.util.HashMap;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.apiintegration.core.model.service.HashMapToDbJsonConverter;
import com.apiintegration.core.model.service.ObjectToDbJsonConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
public class Api {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String apiName;

	private String apiPathUrl;

	private String apiMethod;

	@Column(name = "api_body_object", columnDefinition = "json")
	@Convert(converter = ObjectToDbJsonConverter.class)
	private Object apiBodyObject;

	@Column(name = "api_header_pairs", columnDefinition = "json")
	@Convert(converter = HashMapToDbJsonConverter.class)
	private HashMap<String, String> apiHeaderPairs;

	@Column(name = "api_query_param", columnDefinition = "json")
	@Convert(converter = HashMapToDbJsonConverter.class)
	private HashMap<String, String> apiQueryParam;

	private String apiAuthType;

	private String apiAuthIn;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "services_id")
	private Services services;

	@CreationTimestamp
	Timestamp createdAt;

	@UpdateTimestamp
	Timestamp updatedAt;

	@Version
	private long version;

}
