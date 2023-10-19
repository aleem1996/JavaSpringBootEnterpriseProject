package com.gr.censusmanagement.integration.dto;

import java.util.HashMap;
import java.util.Map;

import com.gr.censusmanagement.constant.CensusConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenericDto {
	private Integer statusCode;
	private String statusMessage;
	private Map<String, Object> detail = new HashMap<>();

	public static GenericDto getSuccessDto(String statusMessage) {
		GenericDto genericDto = new GenericDto();

		genericDto.setStatusCode(CensusConstants.CrmStatusCodes.SUCCESS);
		genericDto.setStatusMessage(statusMessage);

		return genericDto;
	}

	public static GenericDto getFailureDto(String statusMessage) {
		GenericDto genericDto = new GenericDto();

		genericDto.setStatusCode(CensusConstants.CrmStatusCodes.FAILURE);
		genericDto.setStatusMessage(statusMessage);

		return genericDto;
	}
}
