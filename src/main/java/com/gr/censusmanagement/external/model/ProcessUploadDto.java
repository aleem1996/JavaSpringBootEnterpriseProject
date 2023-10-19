package com.gr.censusmanagement.external.model;

import java.util.ArrayList;
import java.util.List;

import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto;
import com.gr.common.v2.exception.model.ApiErrorDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ProcessUploadDto {
	private List<TrcmFormDataRequestDto> trcmFormDataRequestDto;
	private List<DefaultFieldsConfigDto> defaultFieldsConfigs = new ArrayList<DefaultFieldsConfigDto>();
	private List<ApiErrorDto> apiErrors;

}
