package com.gr.censusmanagement.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(Include.NON_EMPTY)
public interface IdDto {
	@ApiModelProperty(example = "ab37d43c-8dd2-4bea-8e67-e82b9ef2bb15")
	String getId();
}
