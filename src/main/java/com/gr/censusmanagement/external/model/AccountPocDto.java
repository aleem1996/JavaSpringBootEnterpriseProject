package com.gr.censusmanagement.external.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AccountPocDto {

	@ApiModelProperty(example = "john")
	private String firstName;

	@ApiModelProperty(example = "doe")
	private String lastName;

	@ApiModelProperty(example = "johndoe@yopmail.com")
	private String email;

	@ApiModelProperty(example = "cafd76ac-ab49-4453-8ea2-8e29b7")
	private String sourcePocId;

	private Boolean isActive = true;
}