package com.gr.censusmanagement.util;

import com.gr.censusmanagement.integration.dto.SubInfoSyncStatusReqDto;
import com.gr.censusmanagement.integration.dto.TravelRecordsSyncStatusReqDto;
import com.gr.censusmanagement.integration.dto.TravelerSyncStatusReqDto;

public class BaseMapTravelDataUtil {
	public static final String TDG_API_MAPPING_001 = "TDG-APIM001";

	public static TravelerSyncStatusReqDto createTravelerSyncStatusForSuccess(String travelerId) {
		TravelerSyncStatusReqDto travelerSyncStatus = new TravelerSyncStatusReqDto();
		travelerSyncStatus.setTravelerId(travelerId);
		travelerSyncStatus.setSyncStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_SUCCESS);
		return travelerSyncStatus;
	}
	
	public static TravelerSyncStatusReqDto createTravelerSyncStatusForFailure(String errorMessage, String travelerId) {
		TravelerSyncStatusReqDto travelerSyncStatus = new TravelerSyncStatusReqDto();
		travelerSyncStatus.setTravelerId(travelerId);
		travelerSyncStatus.setSyncStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_FAILED);
		travelerSyncStatus.getError().setErrorCode(TDG_API_MAPPING_001);
		travelerSyncStatus.getError().setErrorMessage(errorMessage);
		
		return travelerSyncStatus;
	}
	
	public static SubInfoSyncStatusReqDto createSubInfoSyncStatusForFailure(String errorMessage, String globalRescueId) {
		SubInfoSyncStatusReqDto subInfoSyncStatus = new SubInfoSyncStatusReqDto();
		subInfoSyncStatus.setGlobalRescueID(globalRescueId);
		subInfoSyncStatus.setSyncStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_FAILED);
		subInfoSyncStatus.getError().setErrorCode(TDG_API_MAPPING_001);
		subInfoSyncStatus.getError().setErrorMessage(errorMessage);
		
		return subInfoSyncStatus;
	}
	
	public static SubInfoSyncStatusReqDto createSubInfoSyncStatusForSuccess(String globalRescueId) {
		SubInfoSyncStatusReqDto subInfoSyncStatus = new SubInfoSyncStatusReqDto();
		subInfoSyncStatus.setGlobalRescueID(globalRescueId);
		subInfoSyncStatus.setSyncStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_SUCCESS);
		return subInfoSyncStatus;
	}
	
	public static SubInfoSyncStatusReqDto createSubInfoSyncStatusForSuccessWithErrorMessage(String globalRescueId, String errorMessage) {
		SubInfoSyncStatusReqDto subInfoSyncStatus = new SubInfoSyncStatusReqDto();
		subInfoSyncStatus.setGlobalRescueID(globalRescueId);
		subInfoSyncStatus.setSyncStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_SUCCESS);
		subInfoSyncStatus.getError().setErrorCode(TDG_API_MAPPING_001);
		subInfoSyncStatus.getError().setErrorMessage(errorMessage);
		return subInfoSyncStatus;
	}
}
