package com.gr.censusmanagement.validation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.GenericValidator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.censusmanagement.entity.CustomField.DataType;
import com.gr.censusmanagement.external.model.AddressDto;
import com.gr.censusmanagement.external.model.DefaultFieldsConfigDto;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.external.model.request.CustomFieldDataRequestDto;
import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto;
import com.gr.censusmanagement.service.BaseMapTravelDataService;
import com.gr.censusmanagement.service.MessageService;
import com.gr.censusmanagement.service.TrcmFormService;
import com.gr.censusmanagement.util.Util;
import com.gr.common.v2.exception.model.ApiErrorDto;


@Service
public class ValidationService {

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private TrcmFormService trcmFormService;

	@Autowired
	BaseMapTravelDataService baseMapTravelDataService;
	
	public List<ApiErrorDto> validateTrcmFormDataRequest(TrcmFormDataRequestDto trcmFormDataDto) {
		List<ApiErrorDto> apiErrors = new ArrayList<ApiErrorDto>();
		TrcmFormDto trcmFormDto = trcmFormService.getActiveTrcmFormByTrcmFormId(trcmFormDataDto.getTrcmFormId());
		validateRequiredFields(trcmFormDataDto, apiErrors, trcmFormDto.getMembershipTypeOptions());
		validateRequiredCustomField(trcmFormDataDto, apiErrors);
		
		// basemap extra validations
		if ("Form BaseMap".equals(trcmFormDto.getName())) {
			baseMapTravelDataService.validateSingleBaseMapRecord(trcmFormDataDto, apiErrors);			
		}
		
		return apiErrors;
	}

	public void validateRequiredCustomField(TrcmFormDataRequestDto trcmFormDataDto, List<ApiErrorDto> apiErrors) {
		List<CustomFieldDataRequestDto> customFieldDataList = trcmFormDataDto.getCustomFieldData();
		for (CustomFieldDataRequestDto customFieldData : customFieldDataList) {
			if (customFieldData.getCustomField().getDataType().equals(DataType.Address) && !customFieldData.getCustomField().getIsHiddenInForm()) {
				if (customFieldData.getCustomField().getIsRequired().equals(Boolean.TRUE) && Util.isNullOrEmpty(customFieldData.getAddress())) {
					trcmFormDataDto.getApiErrorDtos()
							.add(getApiErrorDto(customFieldData.getCustomField().getLabel().trim(), ErrorCodes.BR_CAS_256, customFieldData.getCustomField().getLabel().trim()));
					apiErrors.add(getApiErrorDto(customFieldData.getCustomField().getLabel().trim(), ErrorCodes.BR_CAS_256, customFieldData.getCustomField().getLabel().trim()));
				}
				if (Util.isNotNull(customFieldData.getAddress()) && customFieldData.getCustomField().getIsRequired().equals(Boolean.TRUE)) {
					validateAddress(customFieldData.getAddress(), apiErrors, trcmFormDataDto);
				}
			} else if (!customFieldData.getCustomField().getIsHiddenInForm()) {
				if (customFieldData.getCustomField().getIsRequired().equals(Boolean.TRUE) && Util.isNullOrEmpty(customFieldData.getValue())) {
					apiErrors.add(getApiErrorDto(customFieldData.getCustomField().getLabel().trim(), ErrorCodes.BR_CAS_256, customFieldData.getCustomField().getLabel().trim()));
					trcmFormDataDto.getApiErrorDtos()
							.add(getApiErrorDto(customFieldData.getCustomField().getLabel().trim(), ErrorCodes.BR_CAS_256, customFieldData.getCustomField().getLabel().trim()));
				}
				if (!Util.isNullOrEmpty(customFieldData.getValue())) {
					if (customFieldData.getCustomField().getDataType().name().equals("PhoneNumber")) {
						validatePhoneNumberField(customFieldData.getCustomField().getLabel().trim(), customFieldData.getValue(), 20, 5, apiErrors, trcmFormDataDto);
					} else if (customFieldData.getCustomField().getDataType().name().equals("Email")) {
						validateEmail(customFieldData.getCustomField().getIsRequired(), customFieldData.getCustomField().getLabel().trim(), customFieldData.getValue(), 100,
								apiErrors, trcmFormDataDto);
					} else if (customFieldData.getCustomField().getDataType().name().equals("ShortText")) {
						validateShortText(customFieldData.getCustomField().getLabel().trim(), customFieldData.getValue(), 255, apiErrors, trcmFormDataDto);
					} else if (customFieldData.getCustomField().getDataType().name().equals("LongText")) {
						validateLongText(customFieldData.getCustomField().getLabel().trim(), customFieldData.getValue(), 2000, apiErrors, trcmFormDataDto);
					} else if (customFieldData.getCustomField().getDataType().name().equals("DropDown")) {
						validateDropDown(customFieldData.getCustomField().getLabel().trim(), customFieldData.getValue(), customFieldData.getCustomField().getOptions(), apiErrors,
								trcmFormDataDto);
					} else if (customFieldData.getCustomField().getDataType().name().equals("Date")) {
						validateDate(customFieldData.getCustomField().getLabel().trim(), customFieldData.getValue(), apiErrors, trcmFormDataDto);
					}
				}
			}
		}
	}

	public void validateAddress(AddressDto address, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		validateField("City", address.getCity(), 250, apiErrors, trcmFormDataDto);
		validateField("Country", address.getCountry(), 250, apiErrors, trcmFormDataDto);
		validateField("LineOne", address.getLineOne(), 250, apiErrors, trcmFormDataDto);

	}

	public void validateField(String fieldName, String field, Integer maxLength, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		if (Util.isNullOrEmpty(fieldName)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));

		}

		if (!Util.maxLength(field, maxLength)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName));
		}
	}

	public void validateRequiredFields(TrcmFormDataRequestDto trcmFormDataDto, List<ApiErrorDto> apiErrors, String membershipTypeOptions) {
		List<DefaultFieldsConfigDto> defaultFieldsConfigs = trcmFormDataDto.getDefaultFieldsConfigs();
		
		for(DefaultFieldsConfigDto defaultFieldsConfig: defaultFieldsConfigs) {
			if(!defaultFieldsConfig.getIsHiddenInForm()) {
				if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("firstName")) {
					validateNameFieldWithRegx(defaultFieldsConfig.getIsRequired(), defaultFieldsConfig.getLabel().trim(), trcmFormDataDto.getFirstName(), 45, apiErrors, trcmFormDataDto);
				} else if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("lastName")) {
					validateNameFieldWithRegx(defaultFieldsConfig.getIsRequired(), defaultFieldsConfig.getLabel().trim(), trcmFormDataDto.getLastName(), 45, apiErrors, trcmFormDataDto);
				} else if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("email")) {
					validateEmail(defaultFieldsConfig.getIsRequired(), defaultFieldsConfig.getLabel().trim(), trcmFormDataDto.getEmail(), 100, apiErrors, trcmFormDataDto);
				} else if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("coverageStartDate")) {
					validateCoverageDates(defaultFieldsConfig.getIsRequired(), trcmFormDataDto.getCoverageStartDate(), trcmFormDataDto.getCoverageEndDate(), defaultFieldsConfig.getLabel().trim(),defaultFieldsConfig.getAttribute().trim(), apiErrors, trcmFormDataDto);
				} else if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("coverageEndDate")) {
					validateCoverageDates(defaultFieldsConfig.getIsRequired(), trcmFormDataDto.getCoverageStartDate(), trcmFormDataDto.getCoverageEndDate(), defaultFieldsConfig.getLabel().trim(), defaultFieldsConfig.getAttribute().trim(), apiErrors, trcmFormDataDto);
				} else if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("dob")) {
					validateDob(defaultFieldsConfig.getIsRequired(), trcmFormDataDto.getDob(), defaultFieldsConfig.getLabel().trim(), apiErrors, trcmFormDataDto);
				} else if (defaultFieldsConfig.getAttribute().trim().equalsIgnoreCase("membershipType")) {
					validateMembershipType(defaultFieldsConfig.getIsRequired(), trcmFormDataDto.getMembershipType(), defaultFieldsConfig.getLabel().trim(), apiErrors, trcmFormDataDto, membershipTypeOptions);
				}
			}
		}
	}
	
	public boolean validateEmail(Boolean isRequired, String fieldName, String fieldValue, int maxLength,  List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		if (isRequired && Util.isNullOrEmpty(fieldValue)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
//			statusMessage.addError(Error.Codes.BR_CAS_200, parent, fieldName);
			return Boolean.FALSE;
		}
		if (!Util.isNullOrEmpty(fieldValue)) {
			Pattern p = Pattern.compile("^['_A-Za-z0-9-\\+]+(\\.['_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(fieldValue);
			if (!m.find()) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
//				statusMessage.addError(Error.Codes.BR_CAS_201, parent, fieldName);
				return Boolean.FALSE;
			}
			
			if (!GenericValidator.maxLength(fieldValue, maxLength)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
//				statusMessage.addError(Error.Codes.BR_CAS_210, parent, fieldName, maxLength);
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	public boolean validateNameFieldWithRegx(Boolean isRequired, String fieldName, String fieldValue, int maxLength, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {

		if (isRequired && Util.isNullOrEmpty(fieldValue)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
			return Boolean.FALSE;
		}
		
		if(Character.isDigit(fieldValue.charAt(0))) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
//			statusMessage.addError(Error.Codes.BR_CAS_006, parent, fieldName);
			return Boolean.FALSE;
		}
		
		if (!Util.isNullOrEmpty(fieldValue)) {
			Pattern p = Pattern.compile("[*$&%_+~^,<>{}\\[\\]\\(\\):;=?@#|]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(fieldValue);

			if (m.find()) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
//				statusMessage.addError(Error.Codes.BR_CAS_006, parent, fieldName);
				return Boolean.FALSE;
			}
			
			p = Pattern.compile("^[\\u0000-\\u007F]", Pattern.CASE_INSENSITIVE);
			m = p.matcher(fieldValue);

			if(!m.find()) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
//				statusMessage.addError(Error.Codes.BR_CAS_006, parent, fieldName);
				return Boolean.FALSE;
			}

			p = Pattern.compile("[a-zA-Z]", Pattern.CASE_INSENSITIVE);
			m = p.matcher(fieldValue);

			if (!m.find()) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_006, fieldName));
//				statusMessage.addError(Error.Codes.BR_CAS_006, parent, fieldName);
				return Boolean.FALSE;
			}

			if (!GenericValidator.minLength(fieldValue, 2)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, "2"));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, "2"));
//				statusMessage.addError(Error.Codes.BR_CAS_212, parent, fieldName, 2);
				return Boolean.FALSE;
			}

			if (!GenericValidator.maxLength(fieldValue, maxLength)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
//				statusMessage.addError(Error.Codes.BR_CAS_210, parent, fieldName, maxLength);
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	public boolean validatePhoneNumberField(String fieldName,
			String fieldValue, int maxLength, int minLength, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		
			if (!GenericValidator.maxLength(fieldValue, maxLength)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
//				statusMessage.addError(Error.Codes.BR_CAS_210, parent, fieldName, maxLength);
				return Boolean.FALSE;
			}
			
			if (!GenericValidator.minLength(fieldValue, minLength)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, minLength));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, minLength));
//				statusMessage.addError(Error.Codes.BR_CAS_212, parent, fieldName, minLength);
				return Boolean.FALSE;
			}
			
			fieldValue = fieldValue.replaceAll("[()\\-\\s]", "");
			
			if(!Pattern.matches("(\\+?[0-9]+)", fieldValue)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
//				statusMessage.addError(Error.Codes.BR_CAS_201, parent, fieldName, minLength);
				return Boolean.FALSE;
			}
		
		return Boolean.TRUE;
	}
	
	
	public boolean validateCoverageDates(Boolean isRequired, Date coverageStartDate, Date coverageEndDate, String fieldName, String attributeName, List<ApiErrorDto> apiErrors,
			TrcmFormDataRequestDto trcmFormDataDto) {

		if (isRequired && Util.isNullOrEmpty(coverageStartDate) && attributeName.equalsIgnoreCase("coverageStartDate")) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
//			statusMessage.addError(Error.Codes.BR_CAS_200, parent, fieldName);
			return Boolean.FALSE;
		}

		if (isRequired && Util.isNullOrEmpty(coverageEndDate) && attributeName.equalsIgnoreCase("coverageEndDate")) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
//			statusMessage.addError(Error.Codes.BR_CAS_200, parent, fieldName);
			return Boolean.FALSE;
		}
		if (!Util.isNullOrEmpty(coverageStartDate) && attributeName.equalsIgnoreCase("coverageStartDate") && Util.isNull(trcmFormDataDto.getId())) {
			Date currentDate = new Date();
		    // Since we dont want to compare time and just the date
		    Calendar calCoverageStartDate = Calendar.getInstance();
		    calCoverageStartDate.setTime(coverageStartDate);
		    calCoverageStartDate.set(Calendar.HOUR_OF_DAY, 0);
		    calCoverageStartDate.set(Calendar.MINUTE, 0);
		    calCoverageStartDate.set(Calendar.SECOND, 0);
		    calCoverageStartDate.set(Calendar.MILLISECOND, 0);
		    Calendar calCurrentDate = Calendar.getInstance();
		    calCurrentDate.setTime(currentDate);
		    calCurrentDate.set(Calendar.HOUR_OF_DAY, 0);
		    calCurrentDate.set(Calendar.MINUTE, 0);
		    calCurrentDate.set(Calendar.SECOND, 0);
		    calCurrentDate.set(Calendar.MILLISECOND, 0);
			if (coverageStartDate.before(currentDate) && !calCoverageStartDate.equals(calCurrentDate)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_214, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_214, fieldName));
				// coverageStartDate is in the past
				return Boolean.FALSE;
			}
		}

		if (!Util.isNullOrEmpty(coverageStartDate) && !Util.isNullOrEmpty(coverageEndDate) && attributeName.equalsIgnoreCase("coverageStartDate")) {
			if (coverageStartDate.after(coverageEndDate)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_215, "Coverage Start Date", "Coverage End Date"));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_215, "Coverage Start Date", "Coverage End Date"));
				// coverageStartDate is after coverageEndDate
				return Boolean.FALSE;
			}
			if (!coverageEndDate.after(coverageStartDate)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_218, "Coverage End Date", "Coverage Start Date"));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_218, "Coverage End Date", "Coverage Start Date"));
				// coverageStartDate is after coverageEndDate
				return Boolean.FALSE;
			}
		}
		return true;
	}
    
	public boolean validateDob(Boolean isRequired, String dob, String fieldName, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		if (isRequired && Util.isNullOrEmpty(dob)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_256, fieldName));
//			statusMessage.addError(Error.Codes.BR_CAS_200, parent, fieldName);
			return Boolean.FALSE;
		}
		if (!Util.isNullOrEmpty(dob)) {
			Date dateOfBirth = Util.formatDate(dob);
			if (Util.isNull(dateOfBirth)) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
				return Boolean.FALSE;
			}
			if (dateOfBirth.after(new Date())) {
				apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_213, fieldName));
				trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_213, fieldName));
				// dob is in the future
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}
    
	public boolean validateShortText(String fieldName, String fieldValue, Integer maxLength, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		if (!GenericValidator.minLength(fieldValue, 2)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, "2"));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, "2"));
//			statusMessage.addError(Error.Codes.BR_CAS_212, parent, fieldName, 2);
			return Boolean.FALSE;
		}

		if (!GenericValidator.maxLength(fieldValue, maxLength)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
//			statusMessage.addError(Error.Codes.BR_CAS_210, parent, fieldName, maxLength);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
    
	public boolean validateLongText(String fieldName, String fieldValue, Integer maxLength, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		if (!GenericValidator.minLength(fieldValue, 10)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, "10"));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_212, fieldName, "10"));
//			statusMessage.addError(Error.Codes.BR_CAS_212, parent, fieldName, 2);
			return Boolean.FALSE;
		}

		if (!GenericValidator.maxLength(fieldValue, maxLength)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_210, fieldName, maxLength));
//			statusMessage.addError(Error.Codes.BR_CAS_210, parent, fieldName, maxLength);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
    
	public boolean validateDropDown(String fieldName, String fieldValue, String options, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {
		String[] dbOptions = options.split(",");

		// Loop through each name and check if it matches the fieldValue string
		for (String dbOption : dbOptions) {
			if (dbOption.equalsIgnoreCase(fieldValue)) {
				return Boolean.TRUE;
			}
		}
		apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_010, fieldName));
		trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_010, fieldName));

		// If no match was found, return false
		return Boolean.FALSE;
	}
    
	public boolean validateDate(String fieldName, String fieldValue, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto) {

		Date dateOfBirth = Util.formatDate(fieldValue);
		if (Util.isNull(dateOfBirth)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_201, fieldName));
		}

		return Boolean.TRUE;
	}
	
	public Boolean validateMembershipType(Boolean isRequired, String membershipType, String fieldName, List<ApiErrorDto> apiErrors, TrcmFormDataRequestDto trcmFormDataDto,
			String membershipOptions) {
		
		if (isRequired && Util.isNullOrEmpty(membershipType)) {
			apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_200, fieldName));
			trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_200, fieldName));
			return Boolean.FALSE;
		}
		JSONArray membershipArray = new JSONArray(membershipOptions);
		for (int i = 0; i < membershipArray.length(); i++) {
			JSONObject membershipObject = membershipArray.getJSONObject(i);

			if (membershipObject.getString("label").equalsIgnoreCase(membershipType)) {
				trcmFormDataDto.setMembershipType(membershipObject.getString("label"));
				return Boolean.TRUE;
			}
		}

		// If no matches were found, return false
		apiErrors.add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_202, fieldName));
		trcmFormDataDto.getApiErrorDtos().add(getApiErrorDto(fieldName, ErrorCodes.BR_CAS_202, fieldName));
		return Boolean.FALSE;

	}

	public ApiErrorDto getApiErrorDto(String fieldName, String errorCode, Object... params) {
		return new ApiErrorDto(fieldName, errorCode, messageService.getFormattedErrorMessage(errorCode, params));
	}

}
