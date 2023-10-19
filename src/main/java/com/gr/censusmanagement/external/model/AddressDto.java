package com.gr.censusmanagement.external.model;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AddressDto {

	private String id;
	private String city;
	private String state;
	private String country;
	private String lineOne;
	private String lineTwo;
	private String latitude;
	private String longitude;
	private Boolean isMilitaryAddress = Boolean.FALSE;
	private String zipCode;
	private Boolean isManual = Boolean.FALSE;
	private Boolean isOptional = Boolean.FALSE;

	@JsonCreator
	public AddressDto(String id, String city, String state, String country, String lineOne, String lineTwo, String zipCode, String latitude, String longitude,
			Boolean isMilitaryAddress, Boolean isManual, Boolean isOptional) {
		this.id = id;
		this.city = city;
		this.state = state;
		this.country = country;
		this.lineOne = lineOne;
		this.lineTwo = lineTwo;
		this.zipCode = zipCode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isMilitaryAddress = isMilitaryAddress;
		this.isManual = isManual;
		this.isOptional = isOptional;
	}
	
	public static List<String> getColumnsNamesForExcel() {
		List<String> addressColumnsList = new ArrayList<String>();
		addressColumnsList.add("City");
		addressColumnsList.add("State");
		addressColumnsList.add("Country");
		addressColumnsList.add("Line 1");
		addressColumnsList.add("Line 2");
		addressColumnsList.add("Zip Code");
		return addressColumnsList;
		
	}
}
