package com.gr.censusmanagement.external.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GridPocSilentLoginRequestDto {
	@ApiModelProperty(example = "3411")
	private String sourceAccountId;
	@ApiModelProperty(example = "7173")
	private String sourcePocId;
}