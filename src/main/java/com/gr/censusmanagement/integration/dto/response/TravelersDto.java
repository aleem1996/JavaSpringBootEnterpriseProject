package com.gr.censusmanagement.integration.dto.response;

import java.util.HashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TravelersDto {
	
	private String firstName;
	private String lastName;
	private String email;
	private String dob;
	private String phone;
//	private List<HashMap<String, String>> customFields;
	private HashMap<String, String> travelerHomeCity;
	private HashMap<String, String> travelerHome;
	private String grTravelerId;
	private String tmcTravelerId;
	private String travelerId;
}
