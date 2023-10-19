package com.gr.censusmanagement.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.gr.censusmanagement.entity.CustomField;
import com.gr.censusmanagement.external.model.CustomFieldDto;

public class CustomFieldMapper extends BaseMapper {
	public static CustomFieldDto toDto(CustomField customField) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		CustomFieldDto customFieldDto = modelMapper.map(customField, CustomFieldDto.class);
		return customFieldDto;
	}

	public static List<CustomFieldDto> toCustomFieldDtoList(List<CustomField> customFieldList) {
		List<CustomFieldDto> customFieldDtoList = customFieldList.stream()
				.map(customField -> new CustomFieldDto(customField.getLabel(), customField.getStatus(), customField.getAttribute(), customField.getDataType(),
						customField.getIsRequired(), customField.getSortable(), customField.getSearchable(), customField.getOptions(), customField.getId(), customField.getFieldInfo(), customField.getSortOrder(), customField.getIsHiddenInForm(), customField.getIsHiddenInTable(), customField.getFieldRegex(), customField.getDisableInEdit()))
				.collect(Collectors.toList());
		return customFieldDtoList;
	}

}
