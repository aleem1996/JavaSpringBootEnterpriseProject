package com.gr.censusmanagement.integration.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TravelRecordsSyncStatusReqDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer cancelledTravelRecords = 0;
	
	private Integer duplicateTravelRecords = 0;
	
	private List<SubInfoSyncStatusReqDto> subInfoSyncStatus = new ArrayList<SubInfoSyncStatusReqDto>();
	
	private String travelDataGatewayRecordLocatorId;
	
	private String travelDataRecordMappingStatus;
	
	private String travelRecordLocatorId;
	
	private String tmcRecordLocatorId;

	private List<TravelerSyncStatusReqDto> travelerSyncStatus = new ArrayList<>();
	
	private Integer updatedTravelersCount = 0;
	
	
	public static final String TRAVEL_RECORD_SYNC_STATUS_SUCCESS = "SUCCESSFULLY_MAPPED";
	public static final String TRAVEL_RECORD_SYNC_STATUS_FAILED = "FAILED_MAPPING";
	public static final String TRAVEL_RECORD_SYNC_STATUS_PARTIAL = "PARTIALLY_MAPPED";
	public static final String TRAVEL_RECORD_SYNC_STATUS_DUPLICATE = "DUPLICATE";
	public static final String TRAVEL_RECORD_SYNC_STATUS_CANCELLED = "CANCELLED";

}
