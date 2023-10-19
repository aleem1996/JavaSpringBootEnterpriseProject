package com.gr.censusmanagement.validation;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.censusmanagement.entity.CustomFieldData;
import com.gr.censusmanagement.entity.TrcmFormData;
import com.gr.censusmanagement.integration.dto.response.TravelersDto;
import com.gr.censusmanagement.service.TrcmFormService;
import com.gr.censusmanagement.util.Util;


@Service
public class BaseMapDataValidationService {

	@Autowired
	TrcmFormService trcmFormService;

	public boolean validateBasemapIdCoupleWithTravelerEmail(String travelerId, String email) {

		TrcmFormData trcmFormData = trcmFormService.getTrcmFormDataByBaseMapId(travelerId);
		if (Util.isNotNull(trcmFormData)) {
			if (trcmFormData.getEmail().equals(email)) {
				return true;
			}
		} else {
			trcmFormData = trcmFormService.getTrcmFormDataForBaseMapByEmail(email);
			if (Util.isNull(trcmFormData)) {
				return true;
			}
		}
		return false;
	}

	public boolean validateSubscriptionStartDateAndEndDate(Date coverageStartDate, Date coverageEndDate,
			String rule, Date feedReceiveDate) {

		if (rule.equals("rule1")) {
			if (coverageStartDate.before(coverageEndDate) || coverageStartDate.equals(coverageEndDate)) {
				return true;
			}
		} else if (rule.equals("rule2")) {

			if (coverageEndDate.after(feedReceiveDate)) {
				return true;
			}
		}
		return false;
	}

	public String validateGlobalRescueIdCoupleWithBaseMapId(String globalRescueId, String basemapId,
			String travelerEmail) {
		TrcmFormData trcmFormData = trcmFormService.getTrcmFormDataByGlobalRescueId(globalRescueId, null);
		if (Util.isNull(trcmFormData)) {
			return "NEW";
		} else {
			String tfdBasemapId = "";
			List<CustomFieldData> customFieldsData = trcmFormData.getCustomFieldData();
			for (CustomFieldData customFieldData : customFieldsData) {
				if ("travelerId".equals(customFieldData.getCustomField().getAttribute())) {
					tfdBasemapId = customFieldData.getValue();
					break;
				}
			}
			if (basemapId.equals(tfdBasemapId)
					&& travelerEmail.equals(trcmFormData.getEmail())) {
				return "UPDATE";
			}
		}
		return "ERROR";
	}

	public boolean validateCoverageDatesOverlapping(String baseMapId, Date newCoverageStartDate,
			Date newCoverageEndDate, String excludedTrcmFormDataId) {
		List<TrcmFormData> trcmFormDataList = trcmFormService.getTrcmFormDataListByBaseMapId(baseMapId, excludedTrcmFormDataId);
		if (Util.isNotNullAndEmpty(trcmFormDataList)) {
			for (TrcmFormData partnerTravelerDataItem : trcmFormDataList) {
				if (doCoverageDatesOverlap(partnerTravelerDataItem, newCoverageStartDate, newCoverageEndDate)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean doCoverageDatesOverlap(TrcmFormData existingTrcmFormData, Date newCoverageStartDate, Date newCoverageEndDate) {

		if ((existingTrcmFormData.getCoverageStartDate().before(newCoverageEndDate)
				|| existingTrcmFormData.getCoverageStartDate().equals(newCoverageEndDate))
				&& (existingTrcmFormData.getCoverageEndDate().after(newCoverageStartDate)
						|| existingTrcmFormData.getCoverageEndDate().equals(newCoverageStartDate))) {
			return true;
		}
		return false;
	}
}
