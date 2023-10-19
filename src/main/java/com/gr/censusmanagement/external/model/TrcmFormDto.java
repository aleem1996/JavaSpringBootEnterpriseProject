package com.gr.censusmanagement.external.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gr.censusmanagement.constant.ErrorCodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TrcmFormDto {

	private String id;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String name;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String status;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean isActive;

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Boolean hasTwoColumnLayout;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String accountId;
	
	private List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
	
	private List<DefaultFieldsConfigDto> defaultFieldsConfigs = new ArrayList<DefaultFieldsConfigDto>();
	
	private String membershipTypeOptions;
	
	private String customScripts;

	@JsonCreator
	public TrcmFormDto(String name, String status, Boolean isActive, List<CustomFieldDto> customFieldDto, List<DefaultFieldsConfigDto> defaultFieldsConfigsDto, String accountId, String membershipTypeOptions, Boolean hasTwoColumnLayout, String customScripts) {
		this.name = name;
		this.status = status;
		this.isActive = isActive;
		this.customFields = customFieldDto;
		this.defaultFieldsConfigs = defaultFieldsConfigsDto;
		this.accountId = accountId;
		this.membershipTypeOptions = membershipTypeOptions;
		this.hasTwoColumnLayout = hasTwoColumnLayout;
		this.customScripts = customScripts;
	}

}
