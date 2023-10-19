package com.gr.censusmanagement.integration.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GridTravelDataAckReqDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String requestId;
	private List<TravelRecordsSyncStatusReqDto> travelRecordsSyncStatus;

}
