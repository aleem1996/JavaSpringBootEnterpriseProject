package com.gr.censusmanagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gr.censusmanagement.common.integration.GridClientService;
import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.entity.TrcmFormData;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.external.model.request.CustomFieldDataRequestDto;
import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto;
import com.gr.censusmanagement.integration.dto.GridTravelDataAckReqDto;
import com.gr.censusmanagement.integration.dto.TravelDataRequestDto;
import com.gr.censusmanagement.integration.dto.TravelRecordsSyncStatusReqDto;
import com.gr.censusmanagement.integration.dto.response.TravelDataDto;
import com.gr.censusmanagement.integration.dto.response.TravelRecordsDto;
import com.gr.censusmanagement.integration.dto.response.TravelersDto;
import com.gr.censusmanagement.model.mapper.TrcmFormDataMapper;
import com.gr.censusmanagement.repository.TrcmFormDataRepository;
import com.gr.censusmanagement.util.BaseMapTravelDataUtil;
import com.gr.censusmanagement.util.Util;
import com.gr.censusmanagement.validation.BaseMapDataValidationService;
import com.gr.common.v2.exception.model.ApiErrorDto;

@Service
public class BaseMapTravelDataService {

	@Autowired
	GridClientService gridClientService;

	@Autowired
	TrcmFormService trcmFormService;

	@Autowired
	TrcmFormDataRepository trcmFormDataRepository;

	@Autowired
	BaseMapDataValidationService basemapDataValidationService;

	@Autowired
	MessageService messageService;

	@Autowired
	AccountService accountService;

	@Value("${gridApiKey}")
	private String apiKey;

	public void getTravelDataFromGrid() {
		TravelDataDto travelDataDto = gridClientService.getTravelData(apiKey, "123",
				TravelDataRequestDto.getTravelDataRequestDto());

		List<TravelRecordsSyncStatusReqDto> travelRecordsSyncStatusDTOsList = new ArrayList<TravelRecordsSyncStatusReqDto>();
		
		// validations
		GridTravelDataAckReqDto gridTravelDataAckReqDto = new GridTravelDataAckReqDto();

		List<TravelRecordsDto> travelRecordsList = travelDataDto.getTravelRecords();
		TravelRecordsSyncStatusReqDto travelRecordsSyncStatusDTO = new TravelRecordsSyncStatusReqDto();

		for (TravelRecordsDto travelRecordDto : travelRecordsList) {
			getTravelerAndCreateAck(travelRecordDto, travelDataDto, travelRecordsSyncStatusDTO, travelRecordDto);
			
			if (Util.isNotNull(travelRecordDto.getBookingReferenceNumber())) {
				travelRecordsSyncStatusDTO.setTravelRecordLocatorId(travelRecordDto.getBookingReferenceNumber());
				travelRecordsSyncStatusDTO.setTravelDataGatewayRecordLocatorId(travelRecordDto.getTravelDataGatewayRecordLocatorId());
			}
			
			travelRecordsSyncStatusDTOsList.add(travelRecordsSyncStatusDTO);
		}

		if (travelRecordsList.size() > 0) {
			gridTravelDataAckReqDto.setTravelRecordsSyncStatus(travelRecordsSyncStatusDTOsList);
			gridTravelDataAckReqDto.setRequestId(UUID.randomUUID().toString());
			gridClientService.acknowledgeTravelData(apiKey, "123", gridTravelDataAckReqDto);			
		}
	}


	private void getTravelerAndCreateAck(TravelRecordsDto travelRecordDto, TravelDataDto travelDataDto, TravelRecordsSyncStatusReqDto travelRecordsSyncStatusDTO, TravelRecordsDto travelRecord) {
		List<TrcmFormDataRequestDto> trcmFormDataRequestList = getTrcmFormDataRequestDtoList(travelDataDto,
				travelRecordsSyncStatusDTO, travelRecord);
		if (Util.isNotNullAndEmpty(trcmFormDataRequestList)) {
			for (TrcmFormDataRequestDto trcmFormDataRequestDto : trcmFormDataRequestList) {
				TrcmFormData trcmFormDataAdded = trcmFormService.saveOrUpdateTrcmFormData(TrcmFormDataMapper.toEntity(trcmFormDataRequestDto));
				
				String baseMapId = null;
				String globalRescueId = null;
				
				for (CustomFieldDataRequestDto customFieldDataDto : trcmFormDataRequestDto.getCustomFieldData()) {
					if ("travelerId".equals(customFieldDataDto.getCustomField().getAttribute())) {
						baseMapId = customFieldDataDto.getValue();
					}
					if ("globalRescueId".equals(customFieldDataDto.getCustomField().getAttribute())) {
						globalRescueId = customFieldDataDto.getValue();
					}
				}
				
				if (basemapDataValidationService.validateCoverageDatesOverlapping(baseMapId,
						trcmFormDataRequestDto.getCoverageStartDate(), trcmFormDataRequestDto.getCoverageEndDate(), trcmFormDataAdded.getId())) {
					travelRecordsSyncStatusDTO.getSubInfoSyncStatus()
							.add(BaseMapTravelDataUtil.createSubInfoSyncStatusForSuccessWithErrorMessage(globalRescueId,
									messageService.getErrorMessage(ErrorCodes.BR_CAS_604)));
				} else {
					travelRecordsSyncStatusDTO.getSubInfoSyncStatus()
							.add(BaseMapTravelDataUtil.createSubInfoSyncStatusForSuccess(globalRescueId));
				}
				travelRecordsSyncStatusDTO.getTravelerSyncStatus()
						.add(BaseMapTravelDataUtil.createTravelerSyncStatusForSuccess(baseMapId));
			}
		} else {
			travelRecordsSyncStatusDTO
					.setTravelDataRecordMappingStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_FAILED);
			return;
		}
		if (travelRecordDto.getSubscriptions().size() == trcmFormDataRequestList.size()) {
			travelRecordsSyncStatusDTO
					.setTravelDataRecordMappingStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_SUCCESS);
		} else if (travelRecordDto.getSubscriptions().size() > trcmFormDataRequestList.size()) {
			travelRecordsSyncStatusDTO
					.setTravelDataRecordMappingStatus(TravelRecordsSyncStatusReqDto.TRAVEL_RECORD_SYNC_STATUS_PARTIAL);
		}
	}

	private List<TrcmFormDataRequestDto> getTrcmFormDataRequestDtoList(TravelDataDto travelDataDto,
			TravelRecordsSyncStatusReqDto travelRecordSyncStatusDto, TravelRecordsDto travelRecord) {
		Account account = accountService.findActiveAccountByName("BaseMap");
		TrcmFormDto trcmFormDto = trcmFormService.getActiveTrcmFormById(account.getId());
		List<TrcmFormDataRequestDto> trcmFormDataRequestList = new ArrayList<>();
		if (travelRecord.getFeedReceiveDate().toString().contains("T")
				&& !travelRecord.getFeedReceiveDate().toString().contains("Z"))
			travelRecord.setFeedReceiveDate(travelRecord.getFeedReceiveDate().toString() + "Z");
		List<TravelersDto> travelers = travelRecord.getTravelers();
		List<HashMap<String, String>> subscriptions = travelRecord.getSubscriptions();

		for (int i = 0; i < subscriptions.size(); i++) {

			validateTravelRecord(trcmFormDto, travelRecordSyncStatusDto, subscriptions.get(i), travelers.get(i),
					trcmFormDataRequestList, travelRecord.getFeedReceiveDate());
		}

		return trcmFormDataRequestList;
	}

	private void validateTravelRecord(TrcmFormDto trcmFormDto, TravelRecordsSyncStatusReqDto travelRecordSyncStatusDto,
			HashMap<String, String> subscription, TravelersDto traveler,
			List<TrcmFormDataRequestDto> trcmFormDataRequestDtoList, String feedDate) {
		String travelerId = null;
		if(Util.isNotNull(traveler.getGrTravelerId())) {
			travelerId = traveler.getGrTravelerId();
		} else if (Util.isNotNull(traveler.getTmcTravelerId())) {
			travelerId = traveler.getTmcTravelerId();
		} else {
			travelerId = traveler.getTravelerId();
		}
		
		if (basemapDataValidationService.validateBasemapIdCoupleWithTravelerEmail(travelerId, traveler.getEmail())) {
			Date coverageEndDate = null;
			Date coverageStartDate = null;
			Date feedReceiveDate = null;
			coverageStartDate = Util.formatDate(Util.getDateFromString(subscription.get("coverageStartDateTime")));
			coverageEndDate = Util.formatDate(Util.getDateFromString(subscription.get("coverageEndDateTime")));
			feedReceiveDate = Util.formatDate(Util.getDateFromString(feedDate));

			if (basemapDataValidationService.validateSubscriptionStartDateAndEndDate(coverageStartDate, coverageEndDate,
					"rule1", null)) {
				if (basemapDataValidationService.validateSubscriptionStartDateAndEndDate(null, coverageEndDate, "rule2",
						feedReceiveDate)) {
					String isNewOrUpdate = basemapDataValidationService.validateGlobalRescueIdCoupleWithBaseMapId(
							subscription.get("globalRescueId"), travelerId, traveler.getEmail());
					if (isNewOrUpdate.equals("NEW")) {
						TrcmFormDataRequestDto trcmFormDataRequest = createTrcmFormDataRequestDto(trcmFormDto,
								subscription, traveler);
						trcmFormDataRequestDtoList.add(trcmFormDataRequest);
					} else if (isNewOrUpdate.equals("UPDATE")) {
						TrcmFormData trcmFormData = trcmFormService
								.getTrcmFormDataByGlobalRescueId(subscription.get("globalRescueId"), null);
						if (trcmFormData.getCoverageEndDate().before(feedReceiveDate)) {
							travelRecordSyncStatusDto.getSubInfoSyncStatus()
									.add(BaseMapTravelDataUtil.createSubInfoSyncStatusForFailure(
											messageService.getErrorMessage(ErrorCodes.BR_CAS_602),
											subscription.get("globalRescueId")));
						} else {
							// TODO: add modified date here
							TrcmFormDataRequestDto trcmFormDataRequest = createTrcmFormDataRequestDto(trcmFormDto,
									subscription, traveler);
							trcmFormDataRequest.setId(trcmFormData.getId());
							trcmFormDataRequestDtoList.add(trcmFormDataRequest);
						}
					} else {
						travelRecordSyncStatusDto.getSubInfoSyncStatus()
								.add(BaseMapTravelDataUtil.createSubInfoSyncStatusForFailure(
										messageService.getErrorMessage(ErrorCodes.BR_CAS_600),
										subscription.get("globalRescueId")));
					}
				} else {
					travelRecordSyncStatusDto.getSubInfoSyncStatus()
							.add(BaseMapTravelDataUtil.createSubInfoSyncStatusForFailure(
									messageService.getErrorMessage(ErrorCodes.BR_CAS_602),
									subscription.get("globalRescueId")));
				}
			} else {
				travelRecordSyncStatusDto.getSubInfoSyncStatus()
						.add(BaseMapTravelDataUtil.createSubInfoSyncStatusForFailure(
								messageService.getErrorMessage(ErrorCodes.BR_CAS_603),
								subscription.get("globalRescueId")));
			}
		} else {
			travelRecordSyncStatusDto.getTravelerSyncStatus()
					.add(BaseMapTravelDataUtil.createTravelerSyncStatusForFailure(
							messageService.getErrorMessage(ErrorCodes.BR_CAS_601), travelerId));
		}
	}

	private TrcmFormDataRequestDto createTrcmFormDataRequestDto(TrcmFormDto trcmFormDto,
			HashMap<String, String> subscription, TravelersDto traveler) throws JSONException {
		TrcmFormDataRequestDto trcmFormDataRequestDto = new TrcmFormDataRequestDto();
		trcmFormDataRequestDto.setFirstName(traveler.getFirstName());
		trcmFormDataRequestDto.setLastName(traveler.getLastName());
		trcmFormDataRequestDto.setEmail(traveler.getEmail());
		trcmFormDataRequestDto.setDob(Util.parseStringDate(traveler.getDob()));
		Integer oMembershipType = Integer.valueOf(subscription.get("basemapMembershipType"));
		String membershipType = TrcmFormService.getMembershipLabel(trcmFormDto.getMembershipTypeOptions(),
				oMembershipType);
		trcmFormDataRequestDto.setMembershipType(membershipType);
		trcmFormDataRequestDto.setCoverageStartDate(
				Util.formatDate(Util.getDateFromString(subscription.get("coverageStartDateTime"))));
		trcmFormDataRequestDto
				.setCoverageEndDate(Util.formatDate(Util.getDateFromString(subscription.get("coverageEndDateTime"))));
		trcmFormDataRequestDto.setTrcmFormId(trcmFormDto.getId());
		trcmFormService.setCustomFieldData(trcmFormDataRequestDto, subscription, trcmFormDto.getCustomFields(),
				traveler);
		if (Util.isNotNull(traveler.getPhone())) {
			trcmFormService.setSelectiveCustomFieldData(trcmFormDataRequestDto, trcmFormDto.getCustomFields(), "phone_number", traveler.getPhone());
		}
		return trcmFormDataRequestDto;
	}
	
	public void validateSingleBaseMapRecord(TrcmFormDataRequestDto trcmFormDataRequest, List<ApiErrorDto> apiErrors) {
		
		String travelerId = "";
		String globalRescueId = "";
		List<CustomFieldDataRequestDto> customFieldsData = trcmFormDataRequest.getCustomFieldData();
		for (CustomFieldDataRequestDto customFieldDataDto : customFieldsData) {
			if ("travelerId".equals(customFieldDataDto.getCustomField().getAttribute())) {
				travelerId = customFieldDataDto.getValue();
			}
			if ("globalRescueId".equals(customFieldDataDto.getCustomField().getAttribute())) {
				globalRescueId = customFieldDataDto.getValue();
			}
		}

		if (basemapDataValidationService.validateBasemapIdCoupleWithTravelerEmail(travelerId, trcmFormDataRequest.getEmail())) {
			if (basemapDataValidationService.validateSubscriptionStartDateAndEndDate(
					trcmFormDataRequest.getCoverageStartDate(), trcmFormDataRequest.getCoverageEndDate(), "rule1",
					null)) {
				Date feedReceiveDate = new Date();
				if (basemapDataValidationService.validateSubscriptionStartDateAndEndDate(null,
						trcmFormDataRequest.getCoverageEndDate(), "rule2", feedReceiveDate)) {
					String isNewOrUpdate = basemapDataValidationService.validateGlobalRescueIdCoupleWithBaseMapId(
							globalRescueId, travelerId, trcmFormDataRequest.getEmail());
					if (isNewOrUpdate.equals("NEW")) {
//						continue;
					} else if (isNewOrUpdate.equals("UPDATE")) {
						TrcmFormData trcmFormData = trcmFormService
								.getTrcmFormDataByGlobalRescueId(globalRescueId, null);
						if (trcmFormData.getCoverageEndDate().before(feedReceiveDate)) {
							apiErrors.add(getApiErrorDto("Coverage Date", ErrorCodes.BR_CAS_602));
							trcmFormDataRequest.getApiErrorDtos()
									.add(getApiErrorDto("Coverage Date", ErrorCodes.BR_CAS_602));
						} else {
							// update scenario
							trcmFormDataRequest.setId(trcmFormData.getId());
						}
					} else {
						apiErrors.add(getApiErrorDto("Global Rescue Id", ErrorCodes.BR_CAS_600));
						trcmFormDataRequest.getApiErrorDtos().add(getApiErrorDto("Global Rescue Id", ErrorCodes.BR_CAS_600));
					}
				} else {
					apiErrors.add(getApiErrorDto("Coverage End Date", ErrorCodes.BR_CAS_602));
					trcmFormDataRequest.getApiErrorDtos().add(getApiErrorDto("Coverage End Date", ErrorCodes.BR_CAS_602));
				}
			} else {
				apiErrors.add(getApiErrorDto("Coverage Start Date", ErrorCodes.BR_CAS_603));
				trcmFormDataRequest.getApiErrorDtos().add(getApiErrorDto("Coverage Start Date", ErrorCodes.BR_CAS_603));
			}
		} else {
			apiErrors.add(getApiErrorDto("Basemap Id", ErrorCodes.BR_CAS_601));
			trcmFormDataRequest.getApiErrorDtos().add(getApiErrorDto("Basemap Id", ErrorCodes.BR_CAS_601));
		}
	}

	private ApiErrorDto getApiErrorDto(String fieldName, String errorCode, Object... params) {
		return new ApiErrorDto(fieldName, errorCode, messageService.getFormattedErrorMessage(errorCode, params));
	}

}
