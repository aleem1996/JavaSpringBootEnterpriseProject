package com.gr.censusmanagement.external.model.request;

import com.gr.censusmanagement.external.model.AddressDto;
import com.gr.censusmanagement.external.model.CustomFieldDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CustomFieldDataRequestDto {

	private String id;
	private String value;
	
	private CustomFieldDto customField;
	private AddressDto address;
}
