package com.gr.censusmanagement.external.model.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.censusmanagement.external.model.DefaultFieldsConfigDto;
import com.gr.common.v2.exception.model.ApiErrorDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TrcmFormDataRequestDto {

	private String id;
	
	private String firstName;

	private String lastName;
	
	private String fullName;
	
	private String dob;
	
	private String email;
	
	private String crmGuid;
	
	private String createdBy;
	
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date createdOn;
	
	private String modifiedBy;
	
	private String source;
	 
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date coverageStartDate;
	
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date coverageEndDate;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String trcmFormId;
	
	private List<CustomFieldDataRequestDto> customFieldData = new ArrayList<CustomFieldDataRequestDto>();
	private List<DefaultFieldsConfigDto> defaultFieldsConfigs = new ArrayList<DefaultFieldsConfigDto>();
	private List<ApiErrorDto> apiErrorDtos = new ArrayList<ApiErrorDto>();
	private Status status;
	
	private String membershipType;
	private Boolean deleted;
	
	public void clearErrors() {
		this.apiErrorDtos.clear();
	}
	
	public enum Status {
		SUCCESSFUL("Successful"), FAILED("Failed");
		private String value;

		private Status(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

}
