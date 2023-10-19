package com.gr.censusmanagement.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Address")
public class Address extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	private String city;
	private String state;
	private String country;
	private String lineOne;
	private String lineTwo;
	private String zipCode;
	private String latitude;
	private String longitude;
	private Boolean isMilitaryAddress = Boolean.FALSE;
	private Boolean isManual = Boolean.FALSE;
	private Boolean isOptional = Boolean.FALSE;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "customFieldId", nullable = false, updatable = false)
//	private CustomField customField;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "customFieldDataId", nullable = false, updatable = false)
	private CustomFieldData customFieldData;
}
