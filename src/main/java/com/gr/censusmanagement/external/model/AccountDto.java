package com.gr.censusmanagement.external.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AccountDto {

	@ApiModelProperty(example = "Acute Angling")
	private String name;

	@ApiModelProperty(example = "cafd76ac-ab49-4453-8ea2-8e29b7")
	private String sourceAccountId;

	private Boolean isActive = true;
	
	private Boolean allowBulkUpload;
	
	private Boolean addTrcmFormData;
	
	private String crmGuid;
	
	private String emailTemplateFileName;
	
	private Boolean allowContactUs;
	
	private String type;

	private String source; 
}
