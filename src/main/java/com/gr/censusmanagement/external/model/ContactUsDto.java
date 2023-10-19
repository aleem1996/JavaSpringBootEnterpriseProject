package com.gr.censusmanagement.external.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.gr.censusmanagement.constant.ErrorCodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ContactUsDto {
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String title;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String message;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String partnerName;
	
	private String phoneNumber;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String accountName;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String partnerEmail;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String emailIsAbout;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String timeZone;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private String regardsOf;
	
	private String accountManager;
}
