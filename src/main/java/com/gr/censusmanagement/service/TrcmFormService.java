package com.gr.censusmanagement.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gr.censusmanagement.constant.CensusConstants;
import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.entity.CustomField;
import com.gr.censusmanagement.entity.CustomFieldData;
import com.gr.censusmanagement.entity.TrcmForm;
import com.gr.censusmanagement.entity.TrcmFormData;
import com.gr.censusmanagement.external.model.AddressDto;
import com.gr.censusmanagement.external.model.CustomFieldDto;
import com.gr.censusmanagement.external.model.DashboardDto;
import com.gr.censusmanagement.external.model.DataTableOptionsDto;
import com.gr.censusmanagement.external.model.DefaultFieldsConfigDto;
import com.gr.censusmanagement.external.model.ExcelFileDataDto;
import com.gr.censusmanagement.external.model.ExportExcelDto;
import com.gr.censusmanagement.external.model.ProcessUploadDto;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.external.model.request.CustomFieldDataRequestDto;
import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto;
import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto.Status;
import com.gr.censusmanagement.integration.dto.CrmUpdateRequestDto;
import com.gr.censusmanagement.integration.dto.ErrorDto;
import com.gr.censusmanagement.integration.dto.GridTravelDataAckReqDto;
import com.gr.censusmanagement.integration.dto.SubInfoSyncStatusReqDto;
import com.gr.censusmanagement.integration.dto.TravelRecordsSyncStatusReqDto;
import com.gr.censusmanagement.integration.dto.TravelerSyncStatusReqDto;
import com.gr.censusmanagement.integration.dto.response.TravelDataDto;
import com.gr.censusmanagement.integration.dto.response.TravelRecordsDto;
import com.gr.censusmanagement.integration.dto.response.TravelersDto;
import com.gr.censusmanagement.model.mapper.TrcmFormDataMapper;
import com.gr.censusmanagement.model.mapper.TrcmFormMapper;
import com.gr.censusmanagement.repository.AccountRepository;
import com.gr.censusmanagement.repository.TrcmFormDataRepository;
import com.gr.censusmanagement.repository.TrcmFormRepository;
import com.gr.censusmanagement.util.CensusExcelUtil;
import com.gr.censusmanagement.util.FileUtil;
import com.gr.censusmanagement.util.Util;
import com.gr.censusmanagement.validation.ValidationService;
import com.gr.common.v2.exception.NotFoundException;
import com.gr.common.v2.exception.model.ApiErrorDto;

@Service
public class TrcmFormService {

	@Autowired
	private TrcmFormRepository trcmFormRepository;

	@Autowired
	private TrcmFormDataRepository trcmFormDataRepository;

	@Autowired
	private ValidationService validationService;

	@Autowired
	private BulkSessionService bulkSessionService;
	
	@Autowired
	private EmailSendService emailSendService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountRepository accountRepository;

	@Value("${app.fileupload.path}")
	private String path;

	private static List<Map<String, String>> excelDataList = new ArrayList<Map<String, String>>();

//	TrcmForm	
	public List<TrcmFormDto> getTrcmFormByAccountId(String accountId) {
		Optional<List<TrcmForm>> oTrcmFormList = trcmFormRepository.findByAccountId(accountId);
		if (!oTrcmFormList.isPresent()) {
			throw NotFoundException.ofResource(TrcmForm.class.getSimpleName(), accountId);
		}
		List<TrcmForm> trcmFormList = oTrcmFormList.get();
		return TrcmFormMapper.toTrcmFormDtoList(trcmFormList);
	}

	// active Forms by AccountId
	@Transactional
	public TrcmFormDto getActiveTrcmFormById(String accountId) {
		Optional<TrcmForm> oTrcmForm = trcmFormRepository.findByAccountIdAndIsActive(accountId, Boolean.TRUE);
		if (!oTrcmForm.isPresent()) {
			throw NotFoundException.ofResource(TrcmForm.class.getSimpleName(), accountId);
		}

		TrcmForm trcmForm = oTrcmForm.get();
		return TrcmFormMapper.toDto(trcmForm);
	}
	
	// active Forms by trcmFormId
	@Transactional
	public TrcmFormDto getActiveTrcmFormByTrcmFormId(String id) {
		Optional<TrcmForm> oTrcmForm = trcmFormRepository.findByIdAndIsActive(id, Boolean.TRUE);
		if (!oTrcmForm.isPresent()) {
			throw NotFoundException.ofResource(TrcmForm.class.getSimpleName(), id);
		}

		TrcmForm trcmForm = oTrcmForm.get();
		return TrcmFormMapper.toDto(trcmForm);
	}

	@Transactional
	public TrcmForm saveOrUpdateTrcmForm(TrcmForm trcmForm) {
		return trcmFormRepository.save(trcmForm);
	}

	@Transactional
	public void deleteTrcmFormById(String trcmFormId) {
		trcmFormRepository.deleteById(trcmFormId);
	}

// TrcmFormData	
	public Page<Map<String, Object>> getTrcmFormDataById(String accountId, DataTableOptionsDto dtOptions, Pageable pageable) {
		PageRequest pageRequest = PageRequest.of(dtOptions.getStart(), dtOptions.getPageSize());
		Page<TrcmFormData> trcmFormData = trcmFormDataRepository.findByAccountId(accountId, dtOptions.getSortColumn(), dtOptions.getAttributeName(), dtOptions.getSortOrder(),
				dtOptions.getFilter(), dtOptions.getCoverageStartDate(), dtOptions.getCoverageEndDate(), dtOptions.getMembershipType(), dtOptions.getFilterType(), pageRequest);
		return TrcmFormDataMapper.toTrcmFormDataDtoMap(trcmFormData);

	}
	
	public Map<Integer,Object> getTrcmFormDataByTrcmFormDataId(String trcmFormDataId) {
		Optional<TrcmFormData> oTrcmFormData = trcmFormDataRepository.findById(trcmFormDataId);
		if (oTrcmFormData.isPresent()) {
			TrcmFormDataRequestDto trcmFormDataRequestDto = TrcmFormDataMapper.toDto(oTrcmFormData.get());
			TrcmFormDto trcmFormDto = getActiveTrcmFormByTrcmFormId(trcmFormDataRequestDto.getTrcmFormId());
			return TrcmFormDataMapper.toTrcmFormDataRequestDtoMap(trcmFormDataRequestDto, trcmFormDto);
		}
		return null;
	}

	public List<DashboardDto> getDashboardData(String accountId, String accountType) {
		return trcmFormDataRepository.getDashboardDto(accountId, accountType);
	}

	@Transactional
	public void deleteTrcmFormDataById(String trcmFormDataId) {
		//soft Deletion
		Optional<TrcmFormData> oTrcmFormData = trcmFormDataRepository.findById(trcmFormDataId);
		if (oTrcmFormData.isPresent()) {
			TrcmFormData trcmFormData = oTrcmFormData.get();
			trcmFormData.setDeleted(Boolean.TRUE);
			trcmFormDataRepository.save(trcmFormData);
		}
		//hard Deletion
//		trcmFormDataRepository.deleteById(trcmFormDataId);
	}

	@Transactional
	public TrcmFormData saveOrUpdateTrcmFormData(TrcmFormData trcmFormData) {
		if (Util.isNullOrEmpty(trcmFormData.getCoverageStartDate())) {
			trcmFormData.setCoverageStartDate(Util.getCurrentDate());
		}
		boolean isEditing = Util.isNotNullAndEmpty(trcmFormData.getId());
		if (!isEditing) {
			checkAndGenerateTheWorldMembershipNumber(trcmFormData);			
		}
		
		trcmFormData.setFullName(trcmFormData.getFirstName().trim() + " " + trcmFormData.getLastName().trim());
		TrcmFormData otrcmFormData = trcmFormDataRepository.save(trcmFormData);
		
		if (!isEditing) {
			String trcmFormId = otrcmFormData.getTrcmForm().getId();
			// getting TrcmForm from db as both trcmform and account were lazily loaded in
			// trcmformData and couldn't be retrieved at same time
			Optional<TrcmForm> oTrcmForm = trcmFormRepository.findByIdAndIsActive(trcmFormId, Boolean.TRUE);
			
			if (oTrcmForm.isPresent()) {
				TrcmForm trcmForm = oTrcmForm.get();
				String emailTemplateFileName = trcmForm.getAccount().getEmailTemplateFileName();
				if (Util.isNotNull(emailTemplateFileName)) {
					String accountId = trcmForm.getAccount().getSourceAccountId();
					emailSendService.sendTheWorldEmail(trcmFormData.getFirstName(), emailTemplateFileName, trcmFormData.getEmail(), accountId);
				}
			}
		}
		return otrcmFormData;
	}

	public ProcessUploadDto processUpload(UUID sessionId) throws IOException {

		excelDataList.clear();
		ProcessUploadDto processUploadDto = new ProcessUploadDto();
		List<ApiErrorDto> apiErrors = new ArrayList<ApiErrorDto>();
		String folderPath = path + (Util.isNullOrEmpty(sessionId.toString()) ? "" : sessionId);
		FileUtil.validateFiles(folderPath, apiErrors, getAllowedHeaders(sessionId));
		if (apiErrors.isEmpty()) {
			String filePath = CensusExcelUtil.getFilesFromPath(folderPath).get(0).getAbsolutePath();
			File file = new File(filePath);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(inputStream);

			Sheet sheet = workbook.getSheet("TrcmFormData");
			int rowCount = sheet.getLastRowNum();
			if (rowCount > CensusConstants.ROW_COUNT) {
				apiErrors.add(ApiErrorDto.of(ErrorCodes.BR_CAS_257, "Trcmform records cannot be more than 200."));
				processUploadDto.setApiErrors(apiErrors);
				return processUploadDto;
			}
			for (int i = 1; i <= rowCount; i++) {
				if(!CensusExcelUtil.isRowEmpty(sheet.getRow(i))) {
					Row row = sheet.getRow(0);
					Map<String, String> rowData = new HashMap<>();
					for (int j = 0; j < row.getLastCellNum(); j++) {
						String key = row.getCell(j).getStringCellValue();
						String value = "";
						if (Util.isNotNull(sheet.getRow(i).getCell(j))) {
							if (sheet.getRow(i).getCell(j).getCellType() == CellType.STRING) {
								value = sheet.getRow(i).getCell(j).getStringCellValue();
							} else if (sheet.getRow(i).getCell(j).getCellType() == CellType.NUMERIC) {
								if (DateUtil.isCellDateFormatted(sheet.getRow(i).getCell(j))) {
									value = Util.formatDate(sheet.getRow(i).getCell(j).getDateCellValue(), "MM/dd/yyyy");
								} else {
									if (key.toLowerCase().indexOf("zip") > -1) {
										Double cellValue = new Double(sheet.getRow(i).getCell(j).getNumericCellValue());
										value = String.valueOf(cellValue.intValue());
									} else {
										value = Double.toString(sheet.getRow(i).getCell(j).getNumericCellValue());
									}
								}
							} else if (sheet.getRow(i).getCell(j).getCellType() == CellType.BOOLEAN) {
								value = String.valueOf(sheet.getRow(i).getCell(j).getBooleanCellValue());
							}
						}
						rowData.put(key, value);
					}
					excelDataList.add(rowData);			
				}
			}

			TrcmFormDto trcmFormDto = getActiveTrcmFormById(bulkSessionService.getSession(sessionId).getAccountId());
			List<CustomFieldDto> customFieldDtoList = unHiddenCustomFields(trcmFormDto.getCustomFields());
			List<DefaultFieldsConfigDto> defaultConfigs = trcmFormDto.getDefaultFieldsConfigs();
			processUploadDto.setTrcmFormDataRequestDto(mapTrcmFormDataFromExcel(sessionId, trcmFormDto, customFieldDtoList, defaultConfigs));
			processUploadDto.setDefaultFieldsConfigs(defaultConfigs);
		}
		processUploadDto.setApiErrors(apiErrors);
		return processUploadDto;
	}

	public List<TrcmFormDataRequestDto> mapTrcmFormDataFromExcel(UUID sessionId, TrcmFormDto trcmFormDto, List<CustomFieldDto> customFieldDtoList, List<DefaultFieldsConfigDto> defaultConfigs) {
		List<TrcmFormDataRequestDto> trcmFormDataRequestDtoList = new ArrayList<TrcmFormDataRequestDto>();
		String trcmFormId = trcmFormDto.getId();
		Boolean primaryMemberCheck = Boolean.FALSE;


		for (int i = 0; i < excelDataList.size(); i++) {
			List<CustomFieldDataRequestDto> customFieldDataRequestDtoList = new ArrayList<CustomFieldDataRequestDto>();
			TrcmFormDataRequestDto trcmFormDataRequestDto = new TrcmFormDataRequestDto();
			trcmFormDataRequestDto.setTrcmFormId(trcmFormId);
			for (DefaultFieldsConfigDto defaultConfig : defaultConfigs) {
				if (!defaultConfig.getIsHiddenInForm()) {
					if (defaultConfig.getAttribute().trim().equalsIgnoreCase("firstName")) {
						trcmFormDataRequestDto.setFirstName(excelDataList.get(i).get(defaultConfig.getLabel().trim()));
					} else if (defaultConfig.getAttribute().trim().equalsIgnoreCase("lastName")) {
						trcmFormDataRequestDto.setLastName(excelDataList.get(i).get(defaultConfig.getLabel().trim()));
					} else if (defaultConfig.getAttribute().trim().equalsIgnoreCase("dob")) {
						trcmFormDataRequestDto.setDob(excelDataList.get(i).get(defaultConfig.getLabel().trim()));
					} else if (defaultConfig.getAttribute().trim().equalsIgnoreCase("email")) {
						trcmFormDataRequestDto.setEmail(excelDataList.get(i).get(defaultConfig.getLabel().trim()));
					} else if (defaultConfig.getAttribute().trim().equalsIgnoreCase("membershipType")) {
						trcmFormDataRequestDto.setMembershipType(excelDataList.get(i).get(defaultConfig.getLabel().trim()));
					} else if (defaultConfig.getAttribute().trim().equalsIgnoreCase("coverageStartDate")) {
						trcmFormDataRequestDto.setCoverageStartDate(Util.formatDate(excelDataList.get(i).get(defaultConfig.getLabel().trim())));
					} else if (defaultConfig.getAttribute().trim().equalsIgnoreCase("coverageEndDate")) {
						trcmFormDataRequestDto.setCoverageEndDate(Util.formatDate(excelDataList.get(i).get(defaultConfig.getLabel().trim())));
					}
				}
			}

			for (int j = 0; j < customFieldDtoList.size(); j++) {
				int index = Arrays.asList(CensusConstants.dataTypes).indexOf(customFieldDtoList.get(j).getDataType().name());
				if (index != -1) {
					if (customFieldDtoList.get(j).getDataType().equals(CustomField.DataType.Address)) {
						List<String> colNames = AddressDto.getColumnsNamesForExcel();
						CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
						AddressDto addressRequestDto = new AddressDto();
						for (int k = 0; k < colNames.size(); k++) {
							if (colNames.get(k).equals("City")) {
								addressRequestDto.setCity(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim() + " " + colNames.get(k)));
							}
							if (colNames.get(k).equals("State")) {
								addressRequestDto.setState(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim() + " " + colNames.get(k)));
							}
							if (colNames.get(k).equals("Country")) {
								addressRequestDto.setCountry(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim() + " " + colNames.get(k)));
							}
							if (colNames.get(k).equals("Line 1")) {
								addressRequestDto.setLineOne(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim() + " " + colNames.get(k)));
							}
							if (colNames.get(k).equals("Line 2")) {
								addressRequestDto.setLineTwo(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim() + " " + colNames.get(k)));
							}
							if (colNames.get(k).equals("Zip Code")) {
								addressRequestDto.setZipCode(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim() + " " + colNames.get(k)));
							}
						}
						customFieldDataRequestDto.setAddress(addressRequestDto);
						customFieldDataRequestDto.setCustomField(customFieldDtoList.get(j));
						customFieldDataRequestDtoList.add(customFieldDataRequestDto);

					}
				} else {
//					now normal field except Address
					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
//					If relationship value is equal to employee then we will not set primary member value
					if ("Relationship".equalsIgnoreCase(customFieldDtoList.get(j).getLabel().trim())) {
						if ("Employee".equalsIgnoreCase(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim()))) {
							primaryMemberCheck = Boolean.TRUE;
						}
					}
					if ("Primary Member".equalsIgnoreCase(customFieldDtoList.get(j).getLabel().trim()) && primaryMemberCheck) {
							customFieldDataRequestDto.setValue("");
					} else {
						customFieldDataRequestDto.setValue(excelDataList.get(i).get(customFieldDtoList.get(j).getLabel().trim()));
					}
					customFieldDataRequestDto.setCustomField(customFieldDtoList.get(j));
					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
				}
			}
			trcmFormDataRequestDto.setDefaultFieldsConfigs(defaultConfigs);
			trcmFormDataRequestDto.setCustomFieldData(customFieldDataRequestDtoList);
			validationService.validateTrcmFormDataRequest(trcmFormDataRequestDto);
			trcmFormDataRequestDtoList.add(trcmFormDataRequestDto);
		}
		return trcmFormDataRequestDtoList;
	}

	@Async
	@Transactional
	public void exportExcelData(String accountId, ExportExcelDto exportExcelData, String sourceName, Pageable pageable) throws IOException {
		Account account = accountService.findById(accountId);
		DataTableOptionsDto dtOptions = exportExcelData.getDataTableOptionsDto();
		PageRequest pageRequest = PageRequest.of(dtOptions.getStart(), dtOptions.getPageSize());
		Page<TrcmFormData> trcmFormData = trcmFormDataRepository.findByAccountId(accountId, dtOptions.getSortColumn(), dtOptions.getAttributeName(), dtOptions.getSortOrder(),
				dtOptions.getFilter(), dtOptions.getCoverageStartDate(), dtOptions.getCoverageEndDate(), dtOptions.getMembershipType(), dtOptions.getFilterType(), pageRequest);
		List<Map<String, Object>> trcmFormDataList = TrcmFormDataMapper.toTrcmFormDataDtoMap(trcmFormData).getContent();

		TrcmFormDto trcmFormDto = getActiveTrcmFormById(accountId);
		List<CustomFieldDto> customFieldDtoList = unHiddenCustomFields(trcmFormDto.getCustomFields());
		List<DefaultFieldsConfigDto> defaultConfigs = trcmFormDto.getDefaultFieldsConfigs();
		sortDefaultFieldList(defaultConfigs);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Travelers");
		List<String> columns = new ArrayList<String>();

		populateHead(defaultConfigs, customFieldDtoList, workbook, sheet, columns, null);
		populateBody(workbook, sheet, columns, trcmFormDataList);

		Long dateMilis = new Date().getTime();

		String folderpath = path + "exportfiles";
		new File(folderpath).mkdirs();
		
		String fileName = File.separator + account.getName() + " - " + "Travelers " + "-" + dateMilis + ".xlsx";
		FileOutputStream out = new FileOutputStream(folderpath + fileName);
		workbook.write(out);
		emailSendService.sendExportEmail("exportfiles" + fileName, account.getName(), exportExcelData.getUploaderEmail(), sourceName, account.getSourceAccountId());
	}
	
	@Transactional
	public ExcelFileDataDto exportExcelDataAndDownload(String accountId, ExportExcelDto exportExcelData, Pageable pageable) throws IOException {
		Account account = accountService.findById(accountId);
		DataTableOptionsDto dtOptions = exportExcelData.getDataTableOptionsDto();
		PageRequest pageRequest = PageRequest.of(dtOptions.getStart(), dtOptions.getPageSize());
		Page<TrcmFormData> trcmFormData = trcmFormDataRepository.findByAccountId(accountId, dtOptions.getSortColumn(), dtOptions.getAttributeName(), dtOptions.getSortOrder(),
				dtOptions.getFilter(), dtOptions.getCoverageStartDate(), dtOptions.getCoverageEndDate(), dtOptions.getMembershipType(), dtOptions.getFilterType(), pageRequest);
		List<Map<String, Object>> trcmFormDataList = TrcmFormDataMapper.toTrcmFormDataDtoMap(trcmFormData).getContent();

		TrcmFormDto trcmFormDto = getActiveTrcmFormById(accountId);
		List<CustomFieldDto> customFieldDtoList = unHiddenCustomFields(trcmFormDto.getCustomFields());
		List<DefaultFieldsConfigDto> defaultConfigs = trcmFormDto.getDefaultFieldsConfigs();
		sortDefaultFieldList(defaultConfigs);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Travelers");
		List<String> columns = new ArrayList<String>();

		populateHead(defaultConfigs, customFieldDtoList, workbook, sheet, columns, null);
		populateBody(workbook, sheet, columns, trcmFormDataList);

//		Long dateMilis = new Date().getTime(); time stamp removed from name
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		String excelFileName = account.getName() + " - " + "Travelers";
		ExcelFileDataDto excelFileExportDto = new ExcelFileDataDto();
		excelFileExportDto.setExcelFileName(excelFileName);
		excelFileExportDto.setFileContent(outputStream.toByteArray());
		return excelFileExportDto;
	}

	public void populateHead(List<DefaultFieldsConfigDto> defaultConfigs, List<CustomFieldDto> customFieldDtoList, XSSFWorkbook workbook, XSSFSheet sheet, List<String> columns,
			String status) {

		Row row = sheet.createRow(0);
		Integer counter = 0;
		for (DefaultFieldsConfigDto defaultConfig : defaultConfigs) {
			if (!defaultConfig.getIsHiddenInForm()) {
				row.createCell(counter).setCellValue(defaultConfig.getLabel().trim());
				columns.add(defaultConfig.getAttribute().trim());
				counter = counter + 1;
			}
		}

		for (int i = 0; i < customFieldDtoList.size(); i++) {
			int index = Arrays.asList(CensusConstants.dataTypes).indexOf(customFieldDtoList.get(i).getDataType().name());
			if (index != -1) {
				if (customFieldDtoList.get(i).getDataType().equals(CustomField.DataType.Address)) {
					List<String> colNames = AddressDto.getColumnsNamesForExcel();
					for (int j = 0; j < colNames.size(); j++) {
						row.createCell(counter).setCellValue(customFieldDtoList.get(i).getLabel().trim() + " " + colNames.get(j));
						columns.add(customFieldDtoList.get(i).getAttribute() + colNames.get(j));
						counter = counter + 1;
					}
				}
			} else {
				row.createCell(counter).setCellValue(customFieldDtoList.get(i).getLabel().trim());
				columns.add(customFieldDtoList.get(i).getAttribute());
				counter = counter + 1;
			}
		}
		
		row.createCell(counter).setCellValue("Creation Date");
		columns.add("createdOn");
		counter = counter + 1;
		
		row.createCell(counter).setCellValue("Modified By");
		columns.add("modifiedBy");
		counter = counter + 1;
		
		if ("Failed".equals(status)) {
			row.createCell(counter).setCellValue("Errors");
			columns.add("errors");
		}
	}

	public void populateBody(XSSFWorkbook workbook, XSSFSheet sheet, List<String> columns, List<Map<String, Object>> trcmFormDataList) {

		for (int i = 1; i <= trcmFormDataList.size(); i++) {
			Row row = sheet.createRow(i);
			populateRow(workbook, sheet, columns, row, trcmFormDataList.get(i - 1));
		}
	}

	public void populateRow(XSSFWorkbook workbook, XSSFSheet sheet, List<String> columns, Row row, Map<String, Object> trcmFormDataList) {
		for (int i = 0; i < columns.size(); i++) {
			if (trcmFormDataList.containsKey(columns.get(i))) {
				if (!Util.isNullOrEmpty(trcmFormDataList.get(columns.get(i)))) {
					String value =  trcmFormDataList.get(columns.get(i)).toString();
					row.createCell(i).setCellValue(value);
				}
			} else {
				row.createCell(i).setCellValue("");
			}
		}

	}

	public String generateExcelReport(UUID sessionId, List<TrcmFormDataRequestDto> trcmFormDataDtolist) throws IOException {

		String accountId = bulkSessionService.getAccount(sessionId);
		TrcmFormDto trcmFormDto = getActiveTrcmFormById(accountId);
		List<DefaultFieldsConfigDto> defaultConfigs = trcmFormDto.getDefaultFieldsConfigs();
		sortDefaultFieldList(defaultConfigs);		
		List<CustomFieldDto> customFieldDtoList = unHiddenCustomFields(trcmFormDto.getCustomFields());
		
		List<TrcmFormDataRequestDto> failedRecords = new ArrayList<TrcmFormDataRequestDto>();
		List<TrcmFormDataRequestDto> successRecords = new ArrayList<TrcmFormDataRequestDto>();
		for (TrcmFormDataRequestDto trcmFormDataDto : trcmFormDataDtolist) {
			if (trcmFormDataDto.getStatus().name().equals("SUCCESSFUL")) {
				successRecords.add(trcmFormDataDto);
				bulkSessionService.updateSuccessfulRecords(sessionId);
			} else {
				failedRecords.add(trcmFormDataDto);
				bulkSessionService.updateUnSuccessfulRecords(sessionId);
			}
		}

		XSSFWorkbook workbook = new XSSFWorkbook();

		if (!successRecords.isEmpty()) {
			List<Map<String, Object>> successTrcmFormDataList = TrcmFormDataMapper.toTrcmFormDataRequestDtoMap(successRecords);
			XSSFSheet successSheet = workbook.createSheet("Success");
			List<String> successColumns = new ArrayList<String>();
			populateHead(defaultConfigs, customFieldDtoList, workbook, successSheet, successColumns, "Passed");
			populateBody(workbook, successSheet, successColumns, successTrcmFormDataList);

		}
		if (!failedRecords.isEmpty()) {
			List<Map<String, Object>> failedTrcmFormDataList = TrcmFormDataMapper.toTrcmFormDataRequestDtoMap(failedRecords);
			XSSFSheet failureSheet = workbook.createSheet("Failed");
			List<String> failureColumns = new ArrayList<String>();
			populateHead(defaultConfigs, customFieldDtoList, workbook, failureSheet, failureColumns, "Failed");
			populateBody(workbook, failureSheet, failureColumns, failedTrcmFormDataList);
		}

		String fileName = CensusConstants.TRCMFORM_STATUS_REPORT_NAME + " (" + Util.formatDateOnlyDashes(new Date()) + ")" + ".xlsx";
		String filePath = sessionId.toString() + File.separator + fileName;

		FileOutputStream out = new FileOutputStream(path + filePath);
		workbook.write(out);
		return filePath;

	}

	public List<String> getAllowedHeaders(UUID sessionId) {
		List<String> allowedHeadersList = new ArrayList<String>();
		String accountId = bulkSessionService.getAccount(sessionId);
		TrcmFormDto trcmFormDto = getActiveTrcmFormById(accountId);
		List<CustomFieldDto> customFieldDtoList = trcmFormDto.getCustomFields();
		List<DefaultFieldsConfigDto> defaultConfigs = trcmFormDto.getDefaultFieldsConfigs();

		for(DefaultFieldsConfigDto defaultConfig: defaultConfigs) {
			if(!defaultConfig.getIsHiddenInForm()) {
				allowedHeadersList.add(defaultConfig.getLabel().trim());
			}
		}
		for (int i = 0; i < customFieldDtoList.size(); i++) {
			if (!customFieldDtoList.get(i).getIsHiddenInForm()) {
				int index = Arrays.asList(CensusConstants.dataTypes).indexOf(customFieldDtoList.get(i).getDataType().name());
				if (index != -1) {
					if (customFieldDtoList.get(i).getDataType().equals(CustomField.DataType.Address)) {
						List<String> colNames = AddressDto.getColumnsNamesForExcel();
						for (int j = 0; j < colNames.size(); j++) {
							allowedHeadersList.add(customFieldDtoList.get(i).getLabel().trim() + " " + colNames.get(j));
						}
					}
				} else {
					allowedHeadersList.add(customFieldDtoList.get(i).getLabel().trim());
				}
			}
		}
		return allowedHeadersList;
	}

	public GridTravelDataAckReqDto saveTrcmFormDataFromGrid(TravelDataDto travelDataDto) {
		TrcmFormDto trcmFormDto = getActiveTrcmFormById("1109");
		String membershipTypeOptions = trcmFormDto.getMembershipTypeOptions(); 
		List<CustomFieldDto> customFieldDtoList = trcmFormDto.getCustomFields();
		GridTravelDataAckReqDto gridTravelDataAckReqDto = new GridTravelDataAckReqDto();
		
		List<TravelRecordsDto> travelRecords = travelDataDto.getTravelRecords();
		for (TravelRecordsDto travelRecord : travelRecords) {
			List<TravelersDto> travelers = travelRecord.getTravelers();
			List<HashMap<String, String>> subscriptions = travelRecord.getSubscriptions();
			for (TravelersDto traveler : travelers) {
				TrcmFormDataRequestDto trcmFormDataRequestDto = new TrcmFormDataRequestDto();
				trcmFormDataRequestDto.setFirstName(traveler.getFirstName());
				trcmFormDataRequestDto.setLastName(traveler.getLastName());
				trcmFormDataRequestDto.setEmail(traveler.getEmail());
				trcmFormDataRequestDto.setDob(Util.parseStringDate(traveler.getDob()));
				Integer oMembershipType = Integer.valueOf(subscriptions.get(0).get("basemapMembershipType"));
				String membershipType = getMembershipLabel(membershipTypeOptions, oMembershipType);
				trcmFormDataRequestDto.setMembershipType(membershipType);
				trcmFormDataRequestDto.setCoverageStartDate(Util.formatDate(Util.getDateFromString(subscriptions.get(0).get("coverageStartDateTime"))));
				trcmFormDataRequestDto.setCoverageEndDate(Util.formatDate(Util.getDateFromString(subscriptions.get(0).get("coverageEndDateTime"))));
				trcmFormDataRequestDto.setTrcmFormId(trcmFormDto.getId());
				setCustomFieldData(trcmFormDataRequestDto, subscriptions.get(0), customFieldDtoList, traveler);
				TrcmFormData trcmFormData = saveOrUpdateTrcmFormData(TrcmFormDataMapper.toEntity(trcmFormDataRequestDto));
				if (!Util.isNullOrEmpty(trcmFormData)) {
					List<TravelRecordsSyncStatusReqDto> travelRecordsSyncStatusReqDtoList = new ArrayList<TravelRecordsSyncStatusReqDto>();
					TravelRecordsSyncStatusReqDto travelRecordsSyncStatusReqDto = new TravelRecordsSyncStatusReqDto();
					List<TravelerSyncStatusReqDto> travelerSyncStatusReqDtoList = new ArrayList<TravelerSyncStatusReqDto>();
					TravelerSyncStatusReqDto travelerSyncStatusReqDto = new TravelerSyncStatusReqDto();
					List<SubInfoSyncStatusReqDto> subInfoSyncStatusReqDtoList = new ArrayList<SubInfoSyncStatusReqDto>();
					SubInfoSyncStatusReqDto subInfoSyncStatusReqDto = new SubInfoSyncStatusReqDto();
					subInfoSyncStatusReqDto.setSyncStatus("SUCCESSFULLY_MAPPED");
					subInfoSyncStatusReqDto.setGlobalRescueID(traveler.getGrTravelerId());
					subInfoSyncStatusReqDtoList.add(subInfoSyncStatusReqDto);
					travelRecordsSyncStatusReqDto.setSubInfoSyncStatus(subInfoSyncStatusReqDtoList);
					travelRecordsSyncStatusReqDto.setTravelDataGatewayRecordLocatorId(travelRecord.getTravelDataGatewayRecordLocatorId());
					travelerSyncStatusReqDto.setSyncStatus("SUCCESSFULLY_MAPPED");
					travelerSyncStatusReqDto.setTravelerId(traveler.getGrTravelerId());
					travelerSyncStatusReqDtoList.add(travelerSyncStatusReqDto);
					travelRecordsSyncStatusReqDto.setTravelerSyncStatus(travelerSyncStatusReqDtoList);
					travelRecordsSyncStatusReqDtoList.add(travelRecordsSyncStatusReqDto);
					gridTravelDataAckReqDto.setTravelRecordsSyncStatus(travelRecordsSyncStatusReqDtoList);
	
				} else {
					List<TravelRecordsSyncStatusReqDto> travelRecordsSyncStatusReqDtoList = new ArrayList<TravelRecordsSyncStatusReqDto>();
					TravelRecordsSyncStatusReqDto travelRecordsSyncStatusReqDto = new TravelRecordsSyncStatusReqDto();
					List<TravelerSyncStatusReqDto> travelerSyncStatusReqDtoList = new ArrayList<TravelerSyncStatusReqDto>();
					TravelerSyncStatusReqDto travelerSyncStatusReqDto = new TravelerSyncStatusReqDto();
					List<SubInfoSyncStatusReqDto> subInfoSyncStatusReqDtoList = new ArrayList<SubInfoSyncStatusReqDto>();
					SubInfoSyncStatusReqDto subInfoSyncStatusReqDto = new SubInfoSyncStatusReqDto();
					ErrorDto error = new ErrorDto();
					subInfoSyncStatusReqDto.setSyncStatus("FAILED_MAPPING");
					subInfoSyncStatusReqDto.setGlobalRescueID(traveler.getGrTravelerId());
					subInfoSyncStatusReqDto.setError(error);
					subInfoSyncStatusReqDtoList.add(subInfoSyncStatusReqDto);
					travelRecordsSyncStatusReqDto.setSubInfoSyncStatus(subInfoSyncStatusReqDtoList);
					travelRecordsSyncStatusReqDto.setTravelDataGatewayRecordLocatorId(travelRecord.getTravelDataGatewayRecordLocatorId());
					travelerSyncStatusReqDto.setSyncStatus("FAILED_MAPPING");
					travelerSyncStatusReqDto.setTravelerId(traveler.getGrTravelerId());
					travelerSyncStatusReqDto.setError(error);
					travelerSyncStatusReqDtoList.add(travelerSyncStatusReqDto);
					travelRecordsSyncStatusReqDto.setTravelerSyncStatus(travelerSyncStatusReqDtoList);
					travelRecordsSyncStatusReqDtoList.add(travelRecordsSyncStatusReqDto);
					gridTravelDataAckReqDto.setTravelRecordsSyncStatus(travelRecordsSyncStatusReqDtoList);
				}
			}
		}
		return gridTravelDataAckReqDto;
		
	}

	public void setCustomFieldData(TrcmFormDataRequestDto trcmFormDataRequestDto, HashMap<String, String> subscription, List<CustomFieldDto> customFieldDtoList,
			TravelersDto traveler) {

		List<CustomFieldDataRequestDto> customFieldDataRequestDtoList = new ArrayList<CustomFieldDataRequestDto>();
		for (String key : subscription.keySet()) {
			String customFieldName = key;
			for (CustomFieldDto customFieldDto : customFieldDtoList) {
				if (customFieldDto.getAttribute().equals(customFieldName) && !customFieldDto.getDataType().name().equals("Address") && !customFieldDto.getDataType().name().equals("Date")) {
					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
					customFieldDataRequestDto.setValue(subscription.get(key));
					customFieldDataRequestDto.setCustomField(customFieldDto);
					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
				}
				if (customFieldDto.getAttribute().equals(customFieldName) && customFieldDto.getDataType().name().equals("Date")) {
					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
					String date = Util.getDateFromString(subscription.get(key));
					customFieldDataRequestDto.setValue(date);
					customFieldDataRequestDto.setCustomField(customFieldDto);
					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
				}
//				else if (customFieldDto.getAttribute().equals(customFieldName) && customFieldDto.getDataType().name().equals("Address")) {
//					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
//					AddressDto addressDto = new AddressDto();
//					addressDto.setCity(traveler.getTravelerHome().get("city"));
//					addressDto.setCountry(traveler.getTravelerHome().get("country"));
//					addressDto.setState(traveler.getTravelerHome().get("state"));
//					customFieldDataRequestDto.setAddress(addressDto);
//					customFieldDataRequestDto.setCustomField(customFieldDto);
//					customFieldDataRequestDto.setValue(null);
//					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
//				}
			}
		}
		HashMap<String, String> travelerHomeInfo = new HashMap<>();
		if (Util.isNotNull(traveler.getTravelerHomeCity())) {
			travelerHomeInfo = traveler.getTravelerHomeCity();
		} else {
			travelerHomeInfo = traveler.getTravelerHome();
		}
		
		if (Util.isNotNull(travelerHomeInfo)) {
			for (CustomFieldDto customFieldDto : customFieldDtoList) {
				if (customFieldDto.getAttribute().equals("home_city")) {
					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
					customFieldDataRequestDto.setCustomField(customFieldDto);
					customFieldDataRequestDto.setValue(travelerHomeInfo.get("city"));
					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
				} else if (customFieldDto.getAttribute().equals("home_state")) {
					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
					customFieldDataRequestDto.setCustomField(customFieldDto);
					customFieldDataRequestDto.setValue(travelerHomeInfo.get("state"));
					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
				} else if (customFieldDto.getAttribute().equals("home_country")) {
					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
					customFieldDataRequestDto.setCustomField(customFieldDto);
					customFieldDataRequestDto.setValue(travelerHomeInfo.get("country"));
					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
				}
			}
		}
		
		if (Util.isNotNull(traveler.getGrTravelerId()) || Util.isNotNull(traveler.getTmcTravelerId()) || Util.isNotNull(traveler.getTravelerId())) {
			for (CustomFieldDto customFieldDto : customFieldDtoList) {
				if (customFieldDto.getAttribute().equals("travelerId")) {
					CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
					customFieldDataRequestDto.setCustomField(customFieldDto);
					if (!Util.isNullOrEmpty(traveler.getGrTravelerId())) {
						customFieldDataRequestDto.setValue(traveler.getGrTravelerId());						
					} else if (!Util.isNullOrEmpty(traveler.getTmcTravelerId())) {
						customFieldDataRequestDto.setValue(traveler.getTmcTravelerId());	
					} else {
						customFieldDataRequestDto.setValue(traveler.getTravelerId());
					}
					customFieldDataRequestDtoList.add(customFieldDataRequestDto);
				}
			}
		}
		
		trcmFormDataRequestDto.setCustomFieldData(customFieldDataRequestDtoList);
	}
	
	public void setSelectiveCustomFieldData(TrcmFormDataRequestDto trcmFormDataRequestDto, List<CustomFieldDto> customFieldDtoList, String customFieldName,
			String customFieldValue) {
		CustomFieldDataRequestDto customFieldDataRequestDto = new CustomFieldDataRequestDto();
		for (CustomFieldDto customFieldDto : customFieldDtoList) {
			if (customFieldDto.getAttribute().equals(customFieldName)) {
				customFieldDataRequestDto.setValue(customFieldValue);
				customFieldDataRequestDto.setCustomField(customFieldDto);
			}
		}
		
		if (Util.isNotNull(customFieldDataRequestDto.getValue())) {			
			trcmFormDataRequestDto.getCustomFieldData().add(customFieldDataRequestDto);
		}
	}
	
	@Async
	@Transactional
	public void saveBulkTrcmFormData(List<TrcmFormDataRequestDto> trcmFormDataDtolist, UUID sessionId, String userName, String accountSource) throws IOException {
		if (bulkSessionService.getSession(sessionId).getStatus().name().equals("PENDING")) {
			String accountId = bulkSessionService.getSession(sessionId).getAccountId();
			Account account = accountService.findById(accountId);
			bulkSessionService.markAsInProgress(sessionId);
			for (TrcmFormDataRequestDto trcmFormDataDto : trcmFormDataDtolist) {
				trcmFormDataDto.clearErrors();
				trcmFormDataDto.setCreatedBy(userName);
				trcmFormDataDto.setModifiedBy(userName);
				trcmFormDataDto.setSource("bulkUpload");
				validationService.validateTrcmFormDataRequest(trcmFormDataDto);
				if (!trcmFormDataDto.getApiErrorDtos().isEmpty()) {
					trcmFormDataDto.setStatus(Status.FAILED);
					bulkSessionService.updateProgress(sessionId);
					continue;
				}
				saveOrUpdateTrcmFormData(TrcmFormDataMapper.toEntity(trcmFormDataDto));
				trcmFormDataDto.setStatus(Status.SUCCESSFUL);
				bulkSessionService.updateProgress(sessionId);
			}
			String statusReportPath = generateExcelReport(sessionId, trcmFormDataDtolist);
			bulkSessionService.getSession(sessionId).setStatusReportPath(statusReportPath);
			emailSendService.sendBulkUploadProcessReportEmail(sessionId, statusReportPath, accountSource, account.getSourceAccountId());
			bulkSessionService.markAsComplete(sessionId);
		}
	}
	
	public static String getMembershipLabel(String membershipTypeOptions, int membershipType) throws JSONException {
		JSONArray membershipArray = new JSONArray(membershipTypeOptions);

		for (int i = 0; i < membershipArray.length(); i++) {
			JSONObject membershipObject = membershipArray.getJSONObject(i);

			if (membershipObject.getInt("type") == membershipType) {
				return membershipObject.getString("label");
			}
		}
		return null;
	}
	
	public List<CustomFieldDto> unHiddenCustomFields(List<CustomFieldDto> customFieldDtoList) {
		List<CustomFieldDto> unHiddencustomFieldDtoList = new ArrayList<CustomFieldDto>();
		for (CustomFieldDto customFieldDto: customFieldDtoList) {
			if (!customFieldDto.getIsHiddenInForm()) {
				unHiddencustomFieldDtoList.add(customFieldDto);
			}
		}
        Collections.sort(unHiddencustomFieldDtoList, new Comparator<CustomFieldDto>() {
            @Override
            public int compare(CustomFieldDto customFieldDto1, CustomFieldDto customFieldDto2) {
                return customFieldDto1.getSortOrder().compareTo(customFieldDto2.getSortOrder());
            }
        });
		return unHiddencustomFieldDtoList;
		
	}	
    public static void sortDefaultFieldList(List<DefaultFieldsConfigDto> defaultConfigs) {
        Collections.sort(defaultConfigs, new Comparator<DefaultFieldsConfigDto>() {
            @Override
            public int compare(DefaultFieldsConfigDto defaultConfigDto1, DefaultFieldsConfigDto defaultConfigDto2) {
                return defaultConfigDto1.getSortOrder().compareTo(defaultConfigDto2.getSortOrder());
            }
        });
    }

	public void updateTraveler(CrmUpdateRequestDto requestBody) {
		Optional<TrcmFormData> oTrcmFormData = trcmFormDataRepository.findById(requestBody.getTrcmFormDataId());
		if (!oTrcmFormData.isPresent()) {
			NotFoundException.ofResource(TrcmFormData.class.getSimpleName(), requestBody.getTrcmFormDataId());
		}

		TrcmFormData trcmFormData = oTrcmFormData.get();
		trcmFormData.setCrmGuid(requestBody.getCrmContactGuid());
		trcmFormDataRepository.save(trcmFormData);		
	}
	
    public TrcmFormData getTrcmFormDataByBaseMapId(String baseMapId) {
    	
    	Optional<List<TrcmFormData>> oTrcmFormDataList = trcmFormDataRepository.findByBaseMapId(baseMapId, null);
		if (oTrcmFormDataList.isPresent()) {			
			List<TrcmFormData> trcmFormDataList = oTrcmFormDataList.get();
			if (Util.isNotNull(trcmFormDataList) && trcmFormDataList.size() > 0) {
				return trcmFormDataList.get(0);			
			}
		}
		return null;
	}
	
	public List<TrcmFormData> getTrcmFormDataListByBaseMapId(String baseMapId, String excludedTrcmFormDataId) {
		Optional<List<TrcmFormData>> oTrcmFormDataList = trcmFormDataRepository.findByBaseMapId(baseMapId, excludedTrcmFormDataId);
		if (oTrcmFormDataList.isPresent()) {
			return trcmFormDataRepository.findByBaseMapId(baseMapId, excludedTrcmFormDataId).get();
		}
		return null;
	}
	
	public TrcmFormData getTrcmFormDataForBaseMapByEmail(String email) {
		Optional<List<TrcmFormData>> oTrcmFormDataList = trcmFormDataRepository.findDataForBaseMapByEmail(email);
		if (oTrcmFormDataList.isPresent()) {
			List<TrcmFormData> trcmFormDataList = oTrcmFormDataList.get();
			if (Util.isNotNull(trcmFormDataList) && trcmFormDataList.size() > 0) {
				return trcmFormDataList.get(0);			
			}
		}
		return null;
	}
	
	public TrcmFormData getTrcmFormDataByGlobalRescueId(String globalRescueId, String excludedTrcmFormDataId) {
		Optional<List<TrcmFormData>> otrcmFormDataList = trcmFormDataRepository.findByGlobalRescueId(globalRescueId, excludedTrcmFormDataId);
		if (otrcmFormDataList.isPresent()) {
			List<TrcmFormData> trcmFormDataList = otrcmFormDataList.get();
			if (Util.isNotNull(trcmFormDataList) && trcmFormDataList.size() > 0) {
				return trcmFormDataList.get(0);			
			}
		}
		return null;
	}
	
	public void sendTheWorldEmail(String accountId, String firstName, String email) {
		Optional<Account> oAccount = accountRepository.findById(accountId);
		if (oAccount.isPresent()) {
			Account account = oAccount.get();
			String emailTemplateFileName = account.getEmailTemplateFileName();
			if (Util.isNotNull(emailTemplateFileName)) {
				emailSendService.sendTheWorldEmail(firstName, emailTemplateFileName, email, accountId);
			}
		}
	}
	
	@Transactional
	public ExcelFileDataDto exportSpecificExcelData(String accountId, String[] trcmFormDataIds) throws IOException {
		List<TrcmFormDataRequestDto> trcmFormDataList = new ArrayList<TrcmFormDataRequestDto>();
		Account account = accountService.findById(accountId);
		Optional<List<TrcmFormData>> otrcmFormDataRecords = trcmFormDataRepository.findTrcmFormDataRecordsById(Arrays.asList(trcmFormDataIds));
		if (otrcmFormDataRecords.isPresent()) {
			List<TrcmFormData> trcmFormDataRecords = otrcmFormDataRecords.get();
			for (TrcmFormData trcmFormDataRecord : trcmFormDataRecords) {
				TrcmFormDataRequestDto trcmFormDataRequestDto = TrcmFormDataMapper.toDto(trcmFormDataRecord);
				trcmFormDataList.add(trcmFormDataRequestDto);
			}
			List<Map<String, Object>> trcmFormDataMapList = TrcmFormDataMapper.toTrcmFormDataRequestDtoMap(trcmFormDataList);
			TrcmFormDto trcmFormDto = getActiveTrcmFormById(accountId);
			List<CustomFieldDto> customFieldDtoList = unHiddenCustomFields(trcmFormDto.getCustomFields());
			List<DefaultFieldsConfigDto> defaultConfigs = trcmFormDto.getDefaultFieldsConfigs();
			sortDefaultFieldList(defaultConfigs);

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Travelers");
			List<String> columns = new ArrayList<String>();

			populateHead(defaultConfigs, customFieldDtoList, workbook, sheet, columns, null);
			populateBody(workbook, sheet, columns, trcmFormDataMapList);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
//			long dateMilis = new Date().getTime(); timestamp removed from name
			String excelFileName = account.getName() + " - " + "Travelers";
			ExcelFileDataDto excelFileExportDto = new ExcelFileDataDto();

			excelFileExportDto.setExcelFileName(excelFileName);
			excelFileExportDto.setFileContent(outputStream.toByteArray());
			return excelFileExportDto;
		}
		return null;
	}
	
	private void checkAndGenerateTheWorldMembershipNumber(TrcmFormData trcmFormData) {
		String id = trcmFormData.getTrcmForm().getId();
		CustomField membershipNumberCustomField = new CustomField();
		Optional<TrcmForm> oTrcmForm = trcmFormRepository.findByIdAndIsActive(id, Boolean.TRUE);
		TrcmForm trcmForm = null;
		if (oTrcmForm.isPresent()) {
			trcmForm = oTrcmForm.get();
		}
		boolean generateNumber = false;
		for (CustomField customField: trcmForm.getCustomFields()) {
			String attribute = customField.getAttribute();
			if ("theWorldMembershipNumber".equalsIgnoreCase(attribute)) {
				generateNumber = true;
				membershipNumberCustomField = customField;
				break;
			}
		}
		
		if (generateNumber) {
			Boolean membershipNumberAlreadyPresentInCFData = false;
			// get last generated number and increment by 1
			String membershipNumber = trcmFormDataRepository.getTheWorldMembershipNumberLastGenerated();
			membershipNumber = membershipNumber.replaceAll("THEWORLD", "");
			Integer mmNumber = Integer.valueOf(membershipNumber);
			mmNumber = mmNumber + 1;
			String newMembershipNumber = "THEWORLD" + String.format("%06d", mmNumber);
			if (!Util.isNullOrEmpty(trcmFormData.getCustomFieldData())) {
				List<CustomFieldData> customFieldsDataList = trcmFormData.getCustomFieldData();
				for (CustomFieldData customFieldData : customFieldsDataList) {
					String attribute = customFieldData.getCustomField().getAttribute();
					if ("theWorldMembershipNumber".equalsIgnoreCase(attribute)) {
						customFieldData.setStrValue(newMembershipNumber);
						customFieldData.setValue(newMembershipNumber);
						membershipNumberAlreadyPresentInCFData = true;
						break;
					}
				}
				
				if (!membershipNumberAlreadyPresentInCFData) {
					CustomFieldData membershipNumberCustomFieldData = new CustomFieldData();
					membershipNumberCustomFieldData.setCustomField(membershipNumberCustomField);
					membershipNumberCustomFieldData.setStrValue(newMembershipNumber);
					membershipNumberCustomFieldData.setValue(newMembershipNumber);
					trcmFormData.getCustomFieldData().add(membershipNumberCustomFieldData);
				}
			}
		}
	}
	
}
