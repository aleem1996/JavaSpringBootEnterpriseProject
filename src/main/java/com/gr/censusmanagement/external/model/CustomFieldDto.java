package com.gr.censusmanagement.external.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.censusmanagement.entity.CustomField.DataType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomFieldDto {

	private String id;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String label;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String attribute;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	@Enumerated(EnumType.STRING)
	private DataType dataType;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean isRequired;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean sortable;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean searchable;
	
	private String fieldInfo;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Integer sortOrder;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean isHiddenInForm;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean isHiddenInTable;
	
	private Boolean disableInEdit;

	private String fieldRegex;
	private String status;
	private String options;

	@JsonCreator
	public CustomFieldDto(String label, String status, String attribute, DataType dataType, Boolean isReq, Boolean sortable, Boolean searchable, String options, String id, String fieldInfo, Integer sortOrder, Boolean isHiddenInForm, Boolean isHiddenInTable, String fieldRegex, Boolean disableInEdit) {
		this.id = id;
		this.label = label;
		this.status = status;
		this.attribute = attribute;
		this.dataType = dataType;
		this.isRequired = isReq;
		this.sortable = sortable;
		this.searchable = searchable;
		this.options = options;
		this.fieldInfo = fieldInfo;
		this.sortOrder = sortOrder;
		this.isHiddenInForm = isHiddenInForm;
		this.isHiddenInTable = isHiddenInTable;
		this.fieldRegex = fieldRegex;
		this.disableInEdit = disableInEdit;
	}

}
