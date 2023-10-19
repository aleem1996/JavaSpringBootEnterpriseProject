package com.gr.censusmanagement.external.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.common.v2.exception.model.ApiErrorDto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TrcmFormDataDto {

	private String id;
	private String firstName;
	private String lastName;
	
	@Setter(AccessLevel.NONE)
	private String dob;
	
	private String email;
	
	@Setter(AccessLevel.NONE)
	private Date coverageStartDate;
	
	@Setter(AccessLevel.NONE)
	private Date coverageEndDate;
	
	private List<CustomFieldDataDto> customFieldData = new ArrayList<CustomFieldDataDto>();
	
	private String membershipType;
	
	@JsonCreator
	public TrcmFormDataDto(String id, String firstName, String lastName, String dob, String email, Date coverageStartDate, Date coverageEndDate, List<CustomFieldDataDto> customFieldDataDto, String membershipType) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.email = email;
		this.coverageStartDate = coverageStartDate;
		this.coverageEndDate = coverageEndDate;
		this.customFieldData = customFieldDataDto;
		this.membershipType = membershipType;
	}

	public void setDob(String dob) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		this.dob =sdf.format(dob);
	}

	public void setCoverageStartDate(Date coverageStartDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date = sdf.format(coverageStartDate);
		try {
			this.coverageStartDate =sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCoverageEndDate(Date coverageEndDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date = sdf.format(coverageEndDate);
		try {
			this.coverageEndDate =sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
