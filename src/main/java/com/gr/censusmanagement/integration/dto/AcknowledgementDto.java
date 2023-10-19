package com.gr.censusmanagement.integration.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AcknowledgementDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String syncStatus;

	private String syncStatusMessage;
	
	private Integer errorCode;
	
	private String errorMessage;
}
