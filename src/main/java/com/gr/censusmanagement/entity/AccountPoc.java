package com.gr.censusmanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AccountPoc extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	@Column(length = 45)
	private String firstName;

	@Column(length = 45)
	private String lastName;

	@Column(length = 100)
	private String email;

	private Boolean isActive;

	@Column(length = 45)
	private String sourcePocId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accountId", nullable = false)
	private Account account;
}
