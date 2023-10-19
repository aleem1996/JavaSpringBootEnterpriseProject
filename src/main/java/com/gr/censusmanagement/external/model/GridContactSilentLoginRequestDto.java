package com.gr.censusmanagement.external.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GridContactSilentLoginRequestDto {
	@ApiModelProperty(example = "John")
	private String firstName;
	@ApiModelProperty(example = "Doe")
	private String lastName;
	@ApiModelProperty(example = "1234")
	private String sourceContactId;
}
