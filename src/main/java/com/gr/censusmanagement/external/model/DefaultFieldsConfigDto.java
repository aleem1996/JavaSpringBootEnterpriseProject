package com.gr.censusmanagement.external.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.entity.DefaultFieldsConfig.DataType;
import com.gr.censusmanagement.util.Util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DefaultFieldsConfigDto {
	private String id;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String label;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String attribute;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean isRequired;
	
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
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	@Enumerated(EnumType.STRING)
	private DataType dataType;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean searchable;
	
	private Boolean disableInEdit;
	private String options;
	private String fieldRegex;

	public DefaultFieldsConfigDto(String id, String label, String attribute, Boolean isRequired, String fieldInfo, Integer sortOrder, Boolean isHiddenInForm, String fieldRegex,
			DataType dataType, String options, Boolean searchable, Boolean disableInEdit, Boolean isHiddenInTable) {
		this.id = id;
		this.label = label;
		this.attribute = attribute;
		this.isRequired = isRequired;
		this.fieldInfo = fieldInfo;
		this.sortOrder = sortOrder;
		this.isHiddenInForm = isHiddenInForm;
		this.fieldRegex = fieldRegex;
		this.dataType = dataType;
		this.options = options;
		this.searchable = searchable;
		this.disableInEdit = disableInEdit;
		this.isHiddenInTable = isHiddenInTable;
	}

	public static List<DefaultFieldsConfigDto> populateDefaultConfigList(String accountSource, String accountType) {
		String defaultMembershipOptions = "Medical Membership,Medical Membership with Security Upgrade";
		List<DefaultFieldsConfigDto> defaultFieldsConfigDtos = new ArrayList<DefaultFieldsConfigDto>();
		DefaultFieldsConfigDto defaultFieldsConfigDtoFirstName = new DefaultFieldsConfigDto(null, "First Name", "firstName", Boolean.TRUE, null, 1, Boolean.FALSE, null,
				DataType.ShortText, null, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
		DefaultFieldsConfigDto defaultFieldsConfigDtoLastName = new DefaultFieldsConfigDto(null, "Last Name", "lastName", Boolean.TRUE, null, 2, Boolean.FALSE, null,
				DataType.ShortText, null, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
		DefaultFieldsConfigDto defaultFieldsConfigDtoFullName = new DefaultFieldsConfigDto(null, "Full Name", "fullName", Boolean.TRUE, null, 2, Boolean.TRUE, null,
				DataType.ShortText, null, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
		DefaultFieldsConfigDto defaultFieldsConfigDtoEmail = new DefaultFieldsConfigDto(null, "Email", "email", Boolean.FALSE, null, 4, Boolean.FALSE, null, DataType.Email, null,
				Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
		DefaultFieldsConfigDto defaultFieldsConfigDtoDob = new DefaultFieldsConfigDto(null, "DOB", "dob", Boolean.FALSE, null, 3, Boolean.FALSE, null, DataType.Date, null,
				Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		
		DefaultFieldsConfigDto defaultFieldsConfigDtoCoverageStartDate = new DefaultFieldsConfigDto();
		DefaultFieldsConfigDto defaultFieldsConfigDtoCoverageEndDate = new DefaultFieldsConfigDto();
		if (Util.isNotNull(accountType)) {
			if ("GRID".equalsIgnoreCase(accountSource)) {
				if ("TRCM_SUBSCRIPTION".equalsIgnoreCase(accountType)) {
					defaultFieldsConfigDtoCoverageStartDate = new DefaultFieldsConfigDto(null, "Coverage Start Date", "coverageStartDate", Boolean.TRUE, null, 6,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);			
				} else {
					defaultFieldsConfigDtoCoverageStartDate = new DefaultFieldsConfigDto(null, "Travel Start Date", "coverageStartDate", Boolean.TRUE, null, 6,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);	
				}
				
				if ("TRCM_SUBSCRIPTION".equalsIgnoreCase(accountType)) {
					defaultFieldsConfigDtoCoverageEndDate = new DefaultFieldsConfigDto(null, "Coverage End Date", "coverageEndDate", Boolean.FALSE, null, 7,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);		
				} else {
					defaultFieldsConfigDtoCoverageEndDate = new DefaultFieldsConfigDto(null, "Travel End Date", "coverageEndDate", Boolean.FALSE, null, 7,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
				}				
			}
			if ("AP".equalsIgnoreCase(accountSource)) {
				if ("Named Traveler".equalsIgnoreCase(accountType)) {
					defaultFieldsConfigDtoCoverageStartDate = new DefaultFieldsConfigDto(null, "Coverage Start Date", "coverageStartDate", Boolean.TRUE, null, 6,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);			
				} else {
					defaultFieldsConfigDtoCoverageStartDate = new DefaultFieldsConfigDto(null, "Travel Start Date", "coverageStartDate", Boolean.TRUE, null, 6,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);	
				}
				
				if ("Named Traveler".equalsIgnoreCase(accountType)) {
					defaultFieldsConfigDtoCoverageEndDate = new DefaultFieldsConfigDto(null, "Coverage End Date", "coverageEndDate", Boolean.FALSE, null, 7,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);		
				} else {
					defaultFieldsConfigDtoCoverageEndDate = new DefaultFieldsConfigDto(null, "Travel End Date", "coverageEndDate", Boolean.FALSE, null, 7,
							Boolean.FALSE, null, DataType.Date, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
				}				
			}
		}
		
		DefaultFieldsConfigDto defaultFieldsConfigDtoMembershipType = new DefaultFieldsConfigDto(null, "Membership Type", "membershipType", Boolean.TRUE, null, 5,
				Boolean.FALSE, null, DataType.DropDown, defaultMembershipOptions, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoFirstName);
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoLastName);
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoFullName);
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoEmail);
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoDob);
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoCoverageStartDate);
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoCoverageEndDate);
		defaultFieldsConfigDtos.add(defaultFieldsConfigDtoMembershipType);
		return defaultFieldsConfigDtos;

	}
	
	

}
