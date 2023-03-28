package com.apiintegration.core.model;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "account_id")
	private Account account;

	@JsonBackReference
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private Set<RelUserProject> projects = new LinkedHashSet<>();

	private String userFullName;

	@Column(name = "user_email")
	private String userEmail;

	@Column(nullable = false)
	private String userPassword;

	@Column(name = "user_role")
	private String userRole;

	private String session;

	@Column(name = "verified_email")
	private Boolean verifiedEmail = false;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	@Version
	private Long version;

	@ToString.Exclude
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL }, mappedBy = "user")
	private Set<UserVisits> userVisits = new LinkedHashSet<>();

	@ToString.Exclude
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL }, mappedBy = "user")
	private Set<Token> tokens = new LinkedHashSet<>();

	public void createAndSetNewSession() {
		this.session = RandomStringUtils.random(64, true, true);
	}

	public void addVisit(UserVisits visit) {
		visit.setUser(this);
		this.userVisits.add(visit);
	}

	public void addProject(RelUserProject project) {
		project.setUser(this);
		this.projects.add(project);
	}

	public void addToken(Token token) {
		token.setUser(this);
		this.tokens.add(token);
	}
}
