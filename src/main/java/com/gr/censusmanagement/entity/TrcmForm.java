package com.gr.censusmanagement.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TrcmForm extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	@Column(length = 45)
	private String name;

	@Column(length = 45)
	private String status;

	private Boolean isActive;
	
	private Boolean hasTwoColumnLayout = Boolean.TRUE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accountId", nullable = false, updatable = false)
	private Account account;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "trcmFormId")
	private List<CustomField> customFields = new ArrayList<CustomField>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "trcmFormId")
	private List<DefaultFieldsConfig> defaultFieldsConfigs = new ArrayList<DefaultFieldsConfig>();
	
	@Lob
	private String membershipTypeOptions;
	
	@Lob
	private String customScripts;

}
