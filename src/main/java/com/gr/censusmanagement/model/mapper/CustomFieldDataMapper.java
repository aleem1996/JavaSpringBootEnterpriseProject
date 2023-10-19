package com.gr.censusmanagement.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.gr.censusmanagement.entity.CustomFieldData;
import com.gr.censusmanagement.external.model.CustomFieldDataDto;

public class CustomFieldDataMapper extends BaseMapper {

	public static CustomFieldDataDto toDto(CustomFieldData customFieldData) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		CustomFieldDataDto customFieldDataDto = modelMapper.map(customFieldData, CustomFieldDataDto.class);
		return customFieldDataDto;
	}

	public static List<CustomFieldDataDto> toCustomFieldDataDtoList(List<CustomFieldData> customFieldDataList) {
		List<CustomFieldDataDto> customFieldDataDtoList = customFieldDataList.stream()
				.map(customFieldData -> new CustomFieldDataDto(customFieldData.getId(), customFieldData.getValue(), customFieldData.getCustomField().getId(), customFieldData.getCustomField().getAttribute(), AddressMapper.toDto(customFieldData.getAddress())))
				.collect(Collectors.toList());
		return customFieldDataDtoList;
	}

}
