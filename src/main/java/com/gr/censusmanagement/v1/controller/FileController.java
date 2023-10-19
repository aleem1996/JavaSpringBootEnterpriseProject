package com.gr.censusmanagement.v1.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gr.auth.annotation.Secured;
import com.gr.censusmanagement.constant.CensusConstants;
import com.gr.censusmanagement.entity.CustomField;
import com.gr.censusmanagement.external.model.AddressDto;
import com.gr.censusmanagement.external.model.CustomFieldDto;
import com.gr.censusmanagement.external.model.DefaultFieldsConfigDto;
import com.gr.censusmanagement.external.model.ExcelFileDataDto;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.service.TrcmFormService;
import com.gr.censusmanagement.util.FileUtil;
import com.gr.common.v2.constant.Constants;
import com.gr.logging.annotation.Loggable;

@Loggable
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = { Constants.Header.X_AUTH_TOKEN })
@Secured
@RestController
@RequestMapping("/api/v1/file")
public class FileController {

	@Autowired
	TrcmFormService trcmFormService;

	@Value("${app.fileupload.path}")
	private String path;

	@PostMapping(path = "/{sessionGuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file, @PathVariable("sessionGuid") String sessionGuid, @RequestPart("batchId") String batchId)
			throws IOException {
		UUID sessionId = UUID.fromString(sessionGuid);
		String fileName = batchId + "_" + file.getOriginalFilename();
		byte[] bytes = file.getBytes();
		FileUtil.writeFile(bytes, fileName, sessionId, batchId, path);
		return ResponseEntity.ok().build();

	}

	@GetMapping(path = "/create/{accountId}")
	public ResponseEntity<ExcelFileDataDto> createExcelSheet(@PathVariable("accountId") String accountId) throws IOException {
		return ResponseEntity.ok().body(createSheetColumns(accountId));
	}

	private ExcelFileDataDto createSheetColumns(String accountId) throws IOException {
		TrcmFormDto trcmFormDto = trcmFormService.getActiveTrcmFormById(accountId);
		List<CustomFieldDto> customFieldDtoList = trcmFormDto.getCustomFields();
		List<DefaultFieldsConfigDto> defaultFieldsConfigs = trcmFormDto.getDefaultFieldsConfigs();
		sortCustomFieldDtoList(customFieldDtoList);
		sortDefaultFieldList(defaultFieldsConfigs);

		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("TrcmFormData");
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		Row row = sheet.createRow(0);
		Integer counter = 0;
		for (int i = 0; i < defaultFieldsConfigs.size(); i++) {
			if(!defaultFieldsConfigs.get(i).getIsHiddenInForm()) {
				Cell cell = row.createCell(counter);
				cell.setCellValue(defaultFieldsConfigs.get(i).getLabel().trim());
				cell.setCellStyle(style);
				counter = counter + 1;
			}
		}
		for (int i = 0; i < customFieldDtoList.size(); i++) {
			if (!customFieldDtoList.get(i).getIsHiddenInForm()) {
				int index = Arrays.asList(CensusConstants.dataTypes).indexOf(customFieldDtoList.get(i).getDataType().name());
				if (index != -1) {
					if (customFieldDtoList.get(i).getDataType().equals(CustomField.DataType.Address)) {
						List<String> colNames = AddressDto.getColumnsNamesForExcel();
						for (int j = 0; j < colNames.size(); j++) {
							Cell cell = row.createCell(counter);
							cell.setCellValue(customFieldDtoList.get(i).getLabel().trim() + " " + colNames.get(j));
							cell.setCellStyle(style);
							counter = counter + 1;
						}
					}
				} else {
					Cell cell = row.createCell(counter);
					cell.setCellValue(customFieldDtoList.get(i).getLabel().trim());
					cell.setCellStyle(style);
					counter = counter + 1;
				}
			}
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		ExcelFileDataDto excelFileDataDto = new ExcelFileDataDto();
		excelFileDataDto.setFileContent(baos.toByteArray());
		baos.close();
		return excelFileDataDto;
//		FileOutputStream out = new FileOutputStream("C:/ExcelFiles/excel.xlsx");
//		workbook.write(out);

	}
	
    public static void sortCustomFieldDtoList(List<CustomFieldDto> customFieldDtoList) {
        Collections.sort(customFieldDtoList, new Comparator<CustomFieldDto>() {
            @Override
            public int compare(CustomFieldDto customFieldDto1, CustomFieldDto customFieldDto2) {
                return customFieldDto1.getSortOrder().compareTo(customFieldDto2.getSortOrder());
            }
        });
    }
    
    public static void sortDefaultFieldList(List<DefaultFieldsConfigDto> defaultConfigs) {
        Collections.sort(defaultConfigs, new Comparator<DefaultFieldsConfigDto>() {
            @Override
            public int compare(DefaultFieldsConfigDto defaultConfigDto1, DefaultFieldsConfigDto defaultConfigDto2) {
                return defaultConfigDto1.getSortOrder().compareTo(defaultConfigDto2.getSortOrder());
            }
        });
    }

}
