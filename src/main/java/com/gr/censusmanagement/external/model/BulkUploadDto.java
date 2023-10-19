package com.gr.censusmanagement.external.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BulkUploadDto {
	
	private String accountId;
	private String accountName;
	private String requestedBy;
	private String uploaderEmail; //bulk upload email will be sent to this email

}
