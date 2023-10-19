package com.gr.censusmanagement.model.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import com.gr.censusmanagement.entity.Address;
import com.gr.censusmanagement.entity.CustomField;
import com.gr.censusmanagement.entity.CustomFieldData;
import com.gr.censusmanagement.entity.TrcmFormData;
import com.gr.censusmanagement.external.model.AddressDto;
import com.gr.censusmanagement.external.model.DefaultFieldsConfigDto;
import com.gr.censusmanagement.external.model.PhoneCallIntakeDto;
import com.gr.censusmanagement.external.model.TrcmFormDataDto;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.external.model.request.CustomFieldDataRequestDto;
import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto;
import com.gr.censusmanagement.util.Util;
import com.gr.common.v2.exception.model.ApiErrorDto;

public class TrcmFormDataMapper extends BaseMapper {

	public static TrcmFormDataRequestDto toDto(TrcmFormData trcmFormData) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		TrcmFormDataRequestDto trcmFormDataDto = modelMapper.map(trcmFormData, TrcmFormDataRequestDto.class);
		return trcmFormDataDto;
	}

	public static TrcmFormData toEntity(TrcmFormDataRequestDto trcmFormDataDto) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		TrcmFormData trcmFormData = modelMapper.map(trcmFormDataDto, TrcmFormData.class);
		for (int i = 0; i < trcmFormData.getCustomFieldData().size(); i++) {
			if (trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("ShortText")
					|| trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("LongText")
					|| trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("DropDown")
					|| trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("Email")
					|| trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("PhoneNumber")) {
				String value = trcmFormData.getCustomFieldData().get(i).getValue();
				trcmFormData.getCustomFieldData().get(i).setStrValue(value);
			} else if (trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("CheckBox")) {
				Boolean value = (trcmFormData.getCustomFieldData().get(i).getValue().equals("true")) ? Boolean.TRUE : Boolean.FALSE;
				trcmFormData.getCustomFieldData().get(i).setBoolValue(value);
			} else if (trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("Address")) {
				trcmFormData.getCustomFieldData().get(i).setValue(null);
				if (trcmFormData.getCustomFieldData().get(i).getAddress() != null) {
					trcmFormData.getCustomFieldData().get(i).getAddress().setCustomFieldData(trcmFormData.getCustomFieldData().get(i));
				}
			} else if (trcmFormData.getCustomFieldData().get(i).getCustomField().getDataType().name().equals("Date")) {								
				Date fomatedDate = Util.formatDate(trcmFormData.getCustomFieldData().get(i).getValue());
				trcmFormData.getCustomFieldData().get(i).setDateValue(fomatedDate);
			}
		}
		return trcmFormData;
	}

	public static List<TrcmFormDataDto> toTrcmFormDataDtoList(List<TrcmFormData> trcmFormDataList) {
		List<TrcmFormDataDto> trcmFormDataDtoList = trcmFormDataList.stream()
				.map(trcmFormData -> new TrcmFormDataDto(trcmFormData.getId(), trcmFormData.getFirstName(), trcmFormData.getLastName(), trcmFormData.getDob(),
						trcmFormData.getEmail(), trcmFormData.getCoverageStartDate(), trcmFormData.getCoverageEndDate(),
						CustomFieldDataMapper.toCustomFieldDataDtoList(trcmFormData.getCustomFieldData()), trcmFormData.getMembershipType()))
				.collect(Collectors.toList());
		return trcmFormDataDtoList;
	}

	public static Page<TrcmFormDataDto> toTrcmFormDataDto(Page<TrcmFormData> trcmFormDataPage) {
		Page<TrcmFormDataDto> trcmFormDataDtoPage = trcmFormDataPage.map(
				trcmFormData -> new TrcmFormDataDto(trcmFormData.getId(), trcmFormData.getFirstName(), trcmFormData.getLastName(), trcmFormData.getDob(), trcmFormData.getEmail(),
						trcmFormData.getCoverageStartDate(), trcmFormData.getCoverageEndDate(), CustomFieldDataMapper.toCustomFieldDataDtoList(trcmFormData.getCustomFieldData()), trcmFormData.getMembershipType()));
		return trcmFormDataDtoPage;
	}

	public static Page<Map<String, Object>> toTrcmFormDataDtoMap(Page<TrcmFormData> trcmFormDataPage) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Page<Map<String, Object>> pageOfHashMap = trcmFormDataPage.map(new Function<TrcmFormData, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(TrcmFormData t) {
				Map<String, Object> hashMap = new HashMap<String, Object>();
				if (!Util.isNullOrEmpty(t.getFirstName())) {
					hashMap.put("firstName", t.getFirstName());

				}
				if (!Util.isNullOrEmpty(t.getLastName())) {
					hashMap.put("lastName", t.getLastName());

				}
				
				if (!Util.isNullOrEmpty(t.getFullName())) {
					hashMap.put("fullName", t.getFullName());
				}
				
				if (!Util.isNullOrEmpty(t.getMembershipType())) {
					hashMap.put("membershipType", t.getMembershipType());

				}
				
				if (!Util.isNullOrEmpty(t.getCoverageStartDate())) {
					hashMap.put("coverageStartDate", sdf.format(t.getCoverageStartDate()));

				}
				if (!Util.isNullOrEmpty(t.getDob())) {
					hashMap.put("dob", t.getDob());
				}

				if (!Util.isNullOrEmpty(t.getEmail())) {
					hashMap.put("email", t.getEmail());
				}

				if (!Util.isNullOrEmpty(t.getCoverageEndDate())) {
					String coverageEndDate = sdf.format(t.getCoverageEndDate());
					hashMap.put("coverageEndDate", coverageEndDate);
				}
				
				if (!Util.isNullOrEmpty(t.getCreatedOn())) {
					String createdOn = sdf.format(t.getCreatedOn());
					hashMap.put("createdOn", createdOn);
				}	
				
				if (!Util.isNullOrEmpty(t.getModifiedBy())) {
					hashMap.put("modifiedBy", t.getModifiedBy());
				}

				if (!Util.isNullOrEmpty(t.getId())) {
					hashMap.put("id", t.getId());
				}

				if (!Util.isNullOrEmpty(t.getCustomFieldData())) {
					List<CustomFieldData> list = t.getCustomFieldData();
					for (CustomFieldData customFieldData : list) {
						String key = customFieldData.getCustomField().getAttribute();
						if (customFieldData.getCustomField().getDataType().equals(CustomField.DataType.Address)) {
							Address address = customFieldData.getAddress();
							if (!Util.isNullOrEmpty(address)) {
								hashMap.put(key, AddressMapper.toDto(address));
								hashMap.put(key + "City", address.getCity());
								hashMap.put(key + "Country", address.getCountry());
								hashMap.put(key + "State", address.getState());
								hashMap.put(key + "Line 1", address.getLineOne());
								hashMap.put(key + "Line 2", address.getLineTwo());
								hashMap.put(key + "Zip Code", address.getZipCode());
							}
						} else {
							String value = customFieldData.getValue();
							if (!Util.isNullOrEmpty(value)) {
								hashMap.put(key, value);
							}
						}
					}
				}
				return hashMap;
			}
		});
		return pageOfHashMap;
	}

	public static List<Map<String, Object>> toTrcmFormDataRequestDtoMap(List<TrcmFormDataRequestDto> trcmFormDataRequestList) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		List<Map<String, Object>> trcmFormDataList = new ArrayList<Map<String, Object>>();

		for (TrcmFormDataRequestDto trcmFormData : trcmFormDataRequestList) {
			Map<String, Object> hashMap = new HashMap<String, Object>();
			if (!Util.isNullOrEmpty(trcmFormData.getFirstName())) {
				hashMap.put("firstName", trcmFormData.getFirstName());
			}
			if (!Util.isNullOrEmpty(trcmFormData.getLastName())) {
				hashMap.put("lastName", trcmFormData.getLastName());
			}
			
			if (!Util.isNullOrEmpty(trcmFormData.getFullName())) {
				hashMap.put("fullName", trcmFormData.getFullName());
			}
			
			if (!Util.isNullOrEmpty(trcmFormData.getMembershipType())) {
				hashMap.put("membershipType", trcmFormData.getMembershipType());
			}
			
			if (!Util.isNullOrEmpty(trcmFormData.getCoverageStartDate())) {
				hashMap.put("coverageStartDate", sdf.format(trcmFormData.getCoverageStartDate()));
			}
			String errors = "";
			if (!trcmFormData.getApiErrorDtos().isEmpty()) {
				for (ApiErrorDto apiErrorDto : trcmFormData.getApiErrorDtos()) {
					errors = errors + apiErrorDto.getMessage() + ". ";
				}
			}
			hashMap.put("errors", errors);

			if (!Util.isNullOrEmpty(trcmFormData.getDob())) {
				hashMap.put("dob", trcmFormData.getDob());
			}

			if (!Util.isNullOrEmpty(trcmFormData.getEmail())) {
				hashMap.put("email", trcmFormData.getEmail());
			}

			if (!Util.isNullOrEmpty(trcmFormData.getCoverageEndDate())) {
				String coverageEndDate = sdf.format(trcmFormData.getCoverageEndDate());
				hashMap.put("coverageEndDate", coverageEndDate);
			}

			if (!Util.isNullOrEmpty(trcmFormData.getCustomFieldData())) {
				List<CustomFieldDataRequestDto> list = trcmFormData.getCustomFieldData();
				for (CustomFieldDataRequestDto customFieldData : list) {
					String key = customFieldData.getCustomField().getAttribute();
					if (customFieldData.getCustomField().getDataType().equals(CustomField.DataType.Address)) {
						AddressDto address = customFieldData.getAddress();
						if (!Util.isNullOrEmpty(address)) {
							hashMap.put(key + "City", address.getCity());
							hashMap.put(key + "Country", address.getCountry());
							hashMap.put(key + "State", address.getState());
							hashMap.put(key + "Line 1", address.getLineOne());
							hashMap.put(key + "Line 2", address.getLineTwo());
							hashMap.put(key + "Zip Code", address.getZipCode());
						}
					} else {
						String value = customFieldData.getValue();
						if (!Util.isNullOrEmpty(value)) {
							hashMap.put(key, value);
						}
					}
				}
			}
			
			if (!Util.isNullOrEmpty(trcmFormData.getCreatedOn())) {
				String createdOn = sdf.format(trcmFormData.getCreatedOn());
				hashMap.put("createdOn", createdOn);
			}
			
			if (!Util.isNullOrEmpty(trcmFormData.getModifiedBy())) {
				hashMap.put("modifiedBy", trcmFormData.getModifiedBy());
			}
			
			trcmFormDataList.add(hashMap);
		}
		return trcmFormDataList;
	}
	
	public static Map<Integer, Object> toTrcmFormDataRequestDtoMap(TrcmFormDataRequestDto trcmFormDataRequest, TrcmFormDto trcmFormDto) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		List<DefaultFieldsConfigDto> defaultFieldsConfigs = trcmFormDto.getDefaultFieldsConfigs();
		Map<Integer, Object> hashMap = new HashMap<Integer, Object>();
		
		for (DefaultFieldsConfigDto defaultFieldsConfig : defaultFieldsConfigs) {
//			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("firstName") && !defaultFieldsConfig.getIsHiddenInForm()) {
//				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
//				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
//				phoneCallIntakeDto.setValue(trcmFormDataRequest.getFirstName());
//				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
//			}
//			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("lastName") && !defaultFieldsConfig.getIsHiddenInForm()) {
//				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
//				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
//				phoneCallIntakeDto.setValue(trcmFormDataRequest.getLastName());
//				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
//			}
			
			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("fullName")) {
				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
				phoneCallIntakeDto.setValue(trcmFormDataRequest.getFullName());
				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
			}
			
			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("membershipType") && !defaultFieldsConfig.getIsHiddenInForm()) {
				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
				phoneCallIntakeDto.setValue(trcmFormDataRequest.getMembershipType());
				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
			}
			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("coverageStartDate") && !defaultFieldsConfig.getIsHiddenInForm()) {
				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
				phoneCallIntakeDto.setValue(sdf.format(trcmFormDataRequest.getCoverageStartDate()));
				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
			}
			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("coverageEndDate") && !defaultFieldsConfig.getIsHiddenInForm()) {
				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
				phoneCallIntakeDto.setValue(sdf.format(trcmFormDataRequest.getCoverageEndDate()));
				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
			}
			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("dob") && !defaultFieldsConfig.getIsHiddenInForm()) {
				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
				phoneCallIntakeDto.setValue(trcmFormDataRequest.getDob());
				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
			}
			if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("email") && !defaultFieldsConfig.getIsHiddenInForm()) {
				PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
				phoneCallIntakeDto.setLabel(defaultFieldsConfig.getLabel().trim());
				phoneCallIntakeDto.setValue(trcmFormDataRequest.getEmail());
				hashMap.put(defaultFieldsConfig.getSortOrder(), phoneCallIntakeDto);
			}
		}
		// added separately because its record is not added in defaultfieldconfig table
		PhoneCallIntakeDto oPhoneCallIntakeDto = new PhoneCallIntakeDto();
		oPhoneCallIntakeDto.setLabel("Created On");
		String createdOn = sdf.format(trcmFormDataRequest.getCreatedOn());
		oPhoneCallIntakeDto.setValue(createdOn);
		hashMap.put(99, oPhoneCallIntakeDto);

		// added separately because its record is not added in defaultfieldconfig table
		PhoneCallIntakeDto phoneCallIntakeDto = new PhoneCallIntakeDto();
		phoneCallIntakeDto.setLabel("Modified By");
		if(Util.isNullOrEmpty(trcmFormDataRequest.getModifiedBy())) {
			phoneCallIntakeDto.setValue(trcmFormDataRequest.getCreatedBy());
		} else {
			phoneCallIntakeDto.setValue(trcmFormDataRequest.getModifiedBy());
		}
		hashMap.put(100, phoneCallIntakeDto);

		if (!Util.isNullOrEmpty(trcmFormDataRequest.getCustomFieldData())) {
			List<CustomFieldDataRequestDto> list = trcmFormDataRequest.getCustomFieldData();
			for (CustomFieldDataRequestDto customFieldData : list) {
				String key = customFieldData.getCustomField().getLabel().trim();
				Boolean isHiddenInForm = customFieldData.getCustomField().getIsHiddenInForm();
				Integer sortOrder = customFieldData.getCustomField().getSortOrder();
				if (customFieldData.getCustomField().getDataType().equals(CustomField.DataType.Address) && !isHiddenInForm) {
					AddressDto address = customFieldData.getAddress();
					PhoneCallIntakeDto phoneCallIntakeDtoCity = new PhoneCallIntakeDto();
					phoneCallIntakeDtoCity.setLabel(key + "City");
					phoneCallIntakeDtoCity.setValue(address.getCity());
					hashMap.put(sortOrder, phoneCallIntakeDtoCity);

					PhoneCallIntakeDto phoneCallIntakeDtoCountry = new PhoneCallIntakeDto();
					phoneCallIntakeDtoCountry.setLabel(key + "Country");
					phoneCallIntakeDtoCountry.setValue(address.getCountry());
					hashMap.put(sortOrder, phoneCallIntakeDtoCountry);

				} else {
					if(!isHiddenInForm) {
						String value = customFieldData.getValue();
						PhoneCallIntakeDto phoneCallIntakeDtoCity = new PhoneCallIntakeDto();
						phoneCallIntakeDtoCity.setLabel(key);
						phoneCallIntakeDtoCity.setValue(value);
						hashMap.put(sortOrder, phoneCallIntakeDtoCity);					
					}
				}
			}
		}
		return hashMap;
	}
}
