package com.gr.censusmanagement.integration.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubInfoSyncStatusReqDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private String globalRescueID;
	private String syncStatus = TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_SUCCESS;
	private ErrorDto error = new ErrorDto();
	
}
