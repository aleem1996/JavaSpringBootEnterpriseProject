package com.gr.censusmanagement.entity;



import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
public class CustomFieldData extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	private String strValue;
	private Integer intValue;
	private Double doubleValue;
	
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date dateValue;
	
	private Boolean boolValue;

	private String value;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customFieldId", nullable = false, updatable = true)
	private CustomField customField;
	
	@OneToOne(mappedBy = "customFieldData", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = true, orphanRemoval= true)
	private Address address;

}
