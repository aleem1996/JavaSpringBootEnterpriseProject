package com.gr.censusmanagement.integration.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TravelDataRequestDto {

	private String requestId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date startDateTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date endDateTime;

	private Integer[] accountIds;

	private int recordsPerPage;

	@NotNull(message = "Cannot be null")
	private int pageNum;

	private int chunkSize;

	public static TravelDataRequestDto getTravelDataRequestDto() {
		TravelDataRequestDto travelDataRequestDto = new TravelDataRequestDto();
		travelDataRequestDto.setAccountIds(new Integer[] { 1109 });
		travelDataRequestDto.setChunkSize(20);
		
//		try {
//			travelDataRequestDto.setStartDateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse("2021-01-01T00:00:00Z"));
//			travelDataRequestDto.setEndDateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse("2023-01-01T00:00:00Z"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		travelDataRequestDto.setPageNum(1);
		travelDataRequestDto.setRecordsPerPage(20);
		travelDataRequestDto.setRequestId("123");

		return travelDataRequestDto;
	}
}
