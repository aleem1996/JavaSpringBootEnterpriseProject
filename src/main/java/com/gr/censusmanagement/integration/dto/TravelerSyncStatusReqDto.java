package com.gr.censusmanagement.integration.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TravelerSyncStatusReqDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String travelerId;
	private String syncStatus;
	private ErrorDto error = new ErrorDto();

}
