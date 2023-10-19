package com.gr.censusmanagement.external.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gr.common.v2.exception.model.ApiErrorDto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel
@JsonInclude(value = Include.NON_NULL)
public class FailureResponseDto {

	private List<ApiErrorDto> errors = new ArrayList<>();
	
	public FailureResponseDto(ApiErrorDto apiError) {
		this.errors.add(apiError);
	}
	
	public FailureResponseDto( List<ApiErrorDto> apiErrors) {
		this.errors = apiErrors;
	}
}
