package com.gr.censusmanagement.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrcmFormDataMapperInterface {

	TrcmFormDataMapperInterface INSTANCE = Mappers.getMapper(TrcmFormDataMapperInterface.class);

//	List<TrcmFormDataDto> toTrcmFormDataDto(List<TrcmFormData> trcmFormDataList);
//
//	List<CustomFieldDataDto> toCustomFieldDatatoDto(List<CustomFieldData> customFieldData);
//
//	CustomFieldDataDto toCustomFieldDataDtoFromCustomField(CustomFieldData customFieldData);

}
