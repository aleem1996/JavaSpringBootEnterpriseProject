package com.gr.censusmanagement.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.gr.censusmanagement.entity.DefaultFieldsConfig;
import com.gr.censusmanagement.external.model.DefaultFieldsConfigDto;

public class DefaultConfigMapper {
	public static List<DefaultFieldsConfigDto> toDefaultConfigDtoList(List<DefaultFieldsConfig> defaultConfigList) {
		List<DefaultFieldsConfigDto> defaultConfigDtoList = defaultConfigList.stream()
				.map(defaultConfig -> new DefaultFieldsConfigDto(defaultConfig.getId(), defaultConfig.getLabel().trim(), defaultConfig.getAttribute(), defaultConfig.getIsRequired(),
						defaultConfig.getFieldInfo(), defaultConfig.getSortOrder(), defaultConfig.getIsHiddenInForm(), defaultConfig.getFieldRegex(), defaultConfig.getDataType(), defaultConfig.getOptions(), defaultConfig.getSearchable(), defaultConfig.getDisableInEdit(), defaultConfig.getIsHiddenInTable()))
				.collect(Collectors.toList());
		return defaultConfigDtoList;
	}
}
