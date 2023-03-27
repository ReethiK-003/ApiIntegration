package com.apiintegration.core.model;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String projectName;

	private String projectDescription;

	private String projectCode;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "account_id")
	private Account account;
	
	@JsonBackReference
	@ToString.Exclude
	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
	private Set<Services> services = new LinkedHashSet<>();

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	@Version
	private Long version;

//	@ToString.Exclude
//	@JsonManagedReference
//	@OneToMany(mappedBy = "project")
//	private Set<RelUserProject> userProjects = new LinkedHashSet<>();

}
