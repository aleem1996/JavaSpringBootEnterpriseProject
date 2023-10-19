package com.gr.censusmanagement.external.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CustomFieldDataDto {

	private String id;
	private String value;
	private String customFieldId;
	private String attribute;
	private AddressDto address;
	
	@JsonCreator
	public CustomFieldDataDto(String id, String value, String customFieldId, String attribute, AddressDto address) {
		this.id = id;
		this.value = value;
		this.customFieldId = customFieldId;
		this.attribute = attribute;
		this.address = address;
	}

}
