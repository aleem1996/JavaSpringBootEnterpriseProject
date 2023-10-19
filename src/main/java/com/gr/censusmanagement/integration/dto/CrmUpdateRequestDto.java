package com.gr.censusmanagement.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmUpdateRequestDto {
	private String crmContactGuid;
	private String type;
	private String crmCaseGuid;
	private Integer gridCaseId;
	private String trcmFormDataId;
	private String caseStatus;
	private String crmCaseNumber;
}
