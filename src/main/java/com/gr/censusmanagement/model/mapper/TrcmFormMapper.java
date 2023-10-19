package com.gr.censusmanagement.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.gr.censusmanagement.entity.TrcmForm;
import com.gr.censusmanagement.external.model.TrcmFormDto;

public class TrcmFormMapper extends BaseMapper {

	public static TrcmFormDto toDto(TrcmForm trcmForm) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		TrcmFormDto trcmFormDto = modelMapper.map(trcmForm, TrcmFormDto.class);
		return trcmFormDto;
	}

	public static TrcmForm toEntity(TrcmFormDto trcmFormDto) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		TrcmForm trcmForm = modelMapper.map(trcmFormDto, TrcmForm.class);
		return trcmForm;
	}

	public static List<TrcmFormDto> toTrcmFormDtoList(List<TrcmForm> trcmFormList) {
		List<TrcmFormDto> trcmFormDataDtoList = trcmFormList.stream()
				.map(trcmForm -> new TrcmFormDto(trcmForm.getName(), trcmForm.getStatus(), trcmForm.getIsActive(),
						CustomFieldMapper.toCustomFieldDtoList(trcmForm.getCustomFields()), DefaultConfigMapper.toDefaultConfigDtoList(trcmForm.getDefaultFieldsConfigs()),
						trcmForm.getAccount().getId(), trcmForm.getMembershipTypeOptions(), trcmForm.getHasTwoColumnLayout(), trcmForm.getCustomScripts()))
				.collect(Collectors.toList());
		return trcmFormDataDtoList;
	}

}
