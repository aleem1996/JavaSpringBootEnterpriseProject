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
public class Account extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	public Account(String accountId) {
		setId(accountId);
	}

	@Column(length = 150)
	private String name;

	@Column(length = 45)
	private String sourceAccountId;

	private Boolean isActive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "systemId", nullable = false, updatable = false)
	private SourceSystem sourceSystem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "apiConfigId", nullable = false, updatable = false)
	private ApiConfiguration apiConfiguration;
	
	private Boolean allowBulkUpload;
	
	private Boolean addTrcmFormData;
	
	private String crmGuid;
	
	private String emailTemplateFileName;
	
	private Boolean allowContactUs;
	
	private String accountManagerEmail;
	
	private String type;

}
