package com.gr.censusmanagement.v1.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gr.auth.annotation.Secured;
import com.gr.auth.service.JwtService;
import com.gr.censusmanagement.constant.CensusConstants;
import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.external.model.BulkUploadDto;
import com.gr.censusmanagement.external.model.ContactUsDto;
import com.gr.censusmanagement.external.model.DashboardDto;
import com.gr.censusmanagement.external.model.DataTableOptionsDto;
import com.gr.censusmanagement.external.model.ExcelFileDataDto;
import com.gr.censusmanagement.external.model.ExportExcelDto;
import com.gr.censusmanagement.external.model.ProcessUploadDto;
import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto;
import com.gr.censusmanagement.model.mapper.TrcmFormDataMapper;
import com.gr.censusmanagement.service.AccountService;
import com.gr.censusmanagement.service.BulkSessionService;
import com.gr.censusmanagement.service.EmailSendService;
import com.gr.censusmanagement.service.TrcmFormService;
import com.gr.censusmanagement.util.ProjectionUtil;
import com.gr.censusmanagement.util.Util;
import com.gr.censusmanagement.validation.ValidationService;
import com.gr.common.v2.constant.Constants;
import com.gr.common.v2.exception.NotFoundException;
import com.gr.common.v2.exception.model.ApiErrorDto;
import com.gr.logging.annotation.Loggable;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Loggable
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = { Constants.Header.X_AUTH_TOKEN })
@Secured
@RestController
@RequestMapping("/api/v1/trcmformdata")
public class TrcmFormDataController {

	@Autowired
	private TrcmFormService trcmFormService;

	@Autowired
	private ValidationService validationService;

	@Autowired
	private EmailSendService emailSendService;

	@Autowired
	private BulkSessionService bulkSessionService;

	@Autowired
	private AccountService accountService;

	@Autowired
	JwtService jwtService;

	@PostMapping("/{accountId}")
	public ResponseEntity<Page<Map<String, Object>>> getTrcmFormDataByAccountId(@PathVariable("accountId") String accountId, @Valid @RequestBody DataTableOptionsDto datatableOptionDto, Pageable pageable) {
		return ResponseEntity.ok(trcmFormService.getTrcmFormDataById(accountId, datatableOptionDto, pageable));

	}

	@GetMapping("/dynamics/{trcmFormDataId}")
	public ResponseEntity<Map<Integer, Object>> getTrcmFormDataByTrcmFormDataId(@PathVariable("trcmFormDataId") String trcmFormDataId) {
		return ResponseEntity.ok(trcmFormService.getTrcmFormDataByTrcmFormDataId(trcmFormDataId));

	}

	@GetMapping("/stats/{accountId}/{accounttype}")
	public ResponseEntity<List<DashboardDto>> getDashboardData(@PathVariable("accountId") String accountId, @PathVariable("accounttype") String accountType) {
		return ResponseEntity.ok(trcmFormService.getDashboardData(accountId, accountType));
	}

	@PostMapping
	public ResponseEntity<?> saveOrUpdateTrcmFormData(@Valid @RequestBody TrcmFormDataRequestDto trcmFormDataDto, @RequestHeader(Constants.Header.X_AUTH_TOKEN) String xAuthToken) throws ParseException {
		List<ApiErrorDto> apiErrors = validationService.validateTrcmFormDataRequest(trcmFormDataDto);
		if (CollectionUtils.isEmpty(apiErrors)) {
			String jwtTokenWithoutPrefix = xAuthToken.substring(7).trim(); // remove "Bearer " prefix
			SignedJWT signedJWT = jwtService.validateAndParseToken(jwtTokenWithoutPrefix);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			String userName = claimsSet.getClaim("firstName").toString() + " " + claimsSet.getClaim("lastName").toString();
			if (Util.isNull(trcmFormDataDto.getId())) {
				trcmFormDataDto.setCreatedBy(userName);
				trcmFormDataDto.setModifiedBy(userName);
			} else {
				trcmFormDataDto.setModifiedBy(userName);
			}
			trcmFormDataDto.setSource("form");
			return ResponseEntity.ok(ProjectionUtil.toIdDto(trcmFormService.saveOrUpdateTrcmFormData(TrcmFormDataMapper.toEntity(trcmFormDataDto))));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrors);
	}

	@PutMapping("/{trcmformDataId}")
	public ResponseEntity<Void> deleteTrcmFormData(@PathVariable("trcmformDataId") String trcmformDataId) {
		trcmFormService.deleteTrcmFormDataById(trcmformDataId);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/bulkdelete")
	public ResponseEntity<Void> deleteTrcmFormData(@RequestBody List<String> trcmFormDataIds) {
		for (String trcmFormDataId : trcmFormDataIds) {
			trcmFormService.deleteTrcmFormDataById(trcmFormDataId);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/bulksession")
	public ResponseEntity<UUID> createBulkSession() {
		UUID sessionId = null;
		sessionId = bulkSessionService.createSession();
		return ResponseEntity.ok(sessionId);
	}

	@PostMapping("/bulk/upload/{sessionGuid}")
	public ResponseEntity<ProcessUploadDto> bulkUpload(@PathVariable("sessionGuid") String sessionGuid, @RequestBody BulkUploadDto bulkUploadDto) throws IOException {
		UUID sessionId = UUID.fromString(sessionGuid);
		if (!bulkSessionService.isSessionExist(sessionId)) {
			bulkSessionService.createSession(sessionId);
		}
		bulkSessionService.getSession(sessionId).setAccountId(bulkUploadDto.getAccountId());
		bulkSessionService.getSession(sessionId).setAccountName(bulkUploadDto.getAccountName());
		bulkSessionService.getSession(sessionId).setRequestedBy(bulkUploadDto.getRequestedBy());
		bulkSessionService.getSession(sessionId).setUploaderEmail(bulkUploadDto.getUploaderEmail());
		ProcessUploadDto processUploadDto = trcmFormService.processUpload(sessionId);
		return ResponseEntity.ok(processUploadDto);
	}

	@PostMapping("/bulk/edit")
	public ResponseEntity<TrcmFormDataRequestDto> editExcelTrcmFormDataRecord(@RequestBody TrcmFormDataRequestDto trcmFormDataDto) {
		trcmFormDataDto.clearErrors();
		validationService.validateTrcmFormDataRequest(trcmFormDataDto);
		return ResponseEntity.ok(trcmFormDataDto);
	}

	@PostMapping("/bulk/save/{sessionGuid}")
	public ResponseEntity<Void> bulkSave(@PathVariable("sessionGuid") String sessionGuid, @RequestBody List<TrcmFormDataRequestDto> trcmFormDataDtolist, @RequestHeader(Constants.Header.X_AUTH_TOKEN) String xAuthToken) throws IOException, ParseException {
		UUID sessionId = UUID.fromString(sessionGuid);
		String jwtTokenWithoutPrefix = xAuthToken.substring(7).trim(); // remove "Bearer " prefix
		SignedJWT signedJWT = jwtService.validateAndParseToken(jwtTokenWithoutPrefix);
		JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
		String source = claimsSet.getClaim("sourceSystemName").toString();

		String userName = claimsSet.getClaim("firstName").toString() + " " + claimsSet.getClaim("lastName").toString();
		trcmFormService.saveBulkTrcmFormData(trcmFormDataDtolist, sessionId, userName, source);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/export/{accountId}")
	public ResponseEntity<ExcelFileDataDto> exportTrcmFormData(@PathVariable("accountId") String accountId, @Valid @RequestBody ExportExcelDto exportExcelData,	@RequestHeader(Constants.Header.X_AUTH_TOKEN) String xAuthToken, Pageable pageable) throws IOException, ParseException {
		String jwtTokenWithoutPrefix = xAuthToken.substring(7).trim(); // remove "Bearer " prefix
		SignedJWT signedJWT = jwtService.validateAndParseToken(jwtTokenWithoutPrefix);
		JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
		String source = claimsSet.getClaim("sourceSystemName").toString();
		trcmFormService.exportExcelData(accountId, exportExcelData, source, pageable);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping(path = "/contactus/{identifier}")
	public ResponseEntity<?> sendContactUsMessage(@PathVariable("identifier") String identifier, @Valid @RequestBody ContactUsDto contactUsDto, @RequestHeader(Constants.Header.X_AUTH_TOKEN) String xAuthToken) throws ParseException {
		if (Util.isNullOrEmpty(identifier)) {
			throw NotFoundException.ofResource(identifier);
		}

		String jwtTokenWithoutPrefix = xAuthToken.substring(7).trim(); // remove "Bearer " prefix
		SignedJWT signedJWT = jwtService.validateAndParseToken(jwtTokenWithoutPrefix);
		JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
		String source = claimsSet.getClaim("sourceSystemName").toString();
		String accountId = claimsSet.getClaim("accountId").toString();

		Account account = accountService.findById(accountId);

		if ("grid".equalsIgnoreCase(source)) {
			if (Util.isNotNull(account.getAccountManagerEmail())) {
				emailSendService.sendContactUsEmailForGrid(contactUsDto, identifier, account.getAccountManagerEmail());
			}
		} else {
			emailSendService.sendContactUsEmailToMS(contactUsDto, identifier);
			emailSendService.sendContactUsEmailToAffinityPartner(contactUsDto, identifier);
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/regardsof")
	public ResponseEntity<String[]> getRegardsOf() throws IOException {

		return ResponseEntity.ok(CensusConstants.regardsOf);
	}
	
	@PostMapping("/sendtheworldemail")
	public ResponseEntity<String> getTheWorldEmail(@RequestBody Map<String, String> jsonData) {
	    String accountId = jsonData.get("accountId");
	    String firstName = jsonData.get("firstName");
	    String email = jsonData.get("email");
	    trcmFormService.sendTheWorldEmail(accountId, firstName, email);

	    return ResponseEntity.ok().build();
	}
	
	@PostMapping("/exportspecificrecords/{accountid}")
	public ResponseEntity<ExcelFileDataDto> exportSpecificTrcmFormData(@PathVariable("accountid") String accountId, @Valid @RequestBody ExportExcelDto exportExcelData) throws IOException {
		return ResponseEntity.ok().body(trcmFormService.exportSpecificExcelData(accountId, exportExcelData.getTrcmFormDataIds()));
	}
	
	@PostMapping("/exportanddownload/{accountid}")
	public ResponseEntity<ExcelFileDataDto> exportAndDownloadTrcmFormData(@PathVariable("accountid") String accountId, @Valid @RequestBody ExportExcelDto exportExcelData, Pageable pageable) throws IOException {
		return ResponseEntity.ok().body(trcmFormService.exportExcelDataAndDownload(accountId, exportExcelData, pageable));
	}

}
