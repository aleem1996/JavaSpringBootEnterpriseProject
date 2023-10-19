package com.gr.censusmanagement.integration.dto.response;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TravelRecordsDto {
	List<TravelersDto> travelers;
	private List<HashMap<String, String>> subscriptions;
	private String travelDataGatewayRecordLocatorId;
	private String feedReceiveDate;
	private String bookingReferenceNumber;

}
