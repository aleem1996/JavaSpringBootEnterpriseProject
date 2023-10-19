package com.gr.censusmanagement.integration.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TravelDataDto {
	private List<TravelRecordsDto> travelRecords;

}
