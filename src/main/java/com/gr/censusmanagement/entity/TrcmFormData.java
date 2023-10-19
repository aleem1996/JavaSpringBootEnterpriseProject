package com.gr.censusmanagement.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TrcmFormData extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	@Column(length = 45)
	private String firstName;

	@Column(length = 45)
	private String lastName;

	@Column(length = 100)
	private String fullName;
	
	private String dob;
	
	@Column(length = 100)
	private String email;
	
	private String crmGuid;
	
	private String createdBy;
	
	private String modifiedBy;
	
	private String source;
	
	@Column
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date coverageStartDate;
	
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date coverageEndDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trcmFormId", nullable = false, updatable = false)
	private TrcmForm trcmForm;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "trcmFormDataId", nullable = false, updatable = true)
	private List<CustomFieldData> customFieldData;
	
	private String membershipType;
	
	private Boolean deleted;

//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//	@JoinColumn(name = "trcmFormDataId", nullable = false, updatable = true)
//	private List<Address> address;
 
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "batchId", nullable = false, updatable = false)
//	private Batch batch;

}
