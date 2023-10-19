package com.gr.censusmanagement.external.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AddressRequestDto {

	private String id;
	private String city;
	private String state;
	private String country;
	private String lineOne;
	private String lineTwo;
	private String zipCode;
	private String latitude;
	private String longitude;
	private Boolean isMilitaryAddress = Boolean.FALSE;
	private Boolean isManual = Boolean.FALSE;
	private Boolean isOptional = Boolean.FALSE;

//	private CustomFieldDto customField;

}
