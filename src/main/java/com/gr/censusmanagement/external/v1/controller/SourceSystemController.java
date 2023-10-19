package com.gr.censusmanagement.external.v1.controller;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gr.auth.annotation.Secured;
import com.gr.auth.annotation.SecuredType;
import com.gr.censusmanagement.external.model.AccountDto;
import com.gr.censusmanagement.external.model.AccountPocDto;
import com.gr.censusmanagement.external.model.GridContactSilentLoginRequestDto;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.external.model.request.TrcmFormDataRequestDto;
import com.gr.censusmanagement.external.v1.api.SourceSystemApi;
import com.gr.censusmanagement.model.mapper.AccountMapper;
import com.gr.censusmanagement.model.mapper.AccountPocMapper;
import com.gr.censusmanagement.model.mapper.TrcmFormDataMapper;
import com.gr.censusmanagement.model.response.IdDto;
import com.gr.censusmanagement.service.AccountPocService;
import com.gr.censusmanagement.service.AccountService;
import com.gr.censusmanagement.service.AuthService;
import com.gr.censusmanagement.service.TrcmFormService;
import com.gr.censusmanagement.util.ProjectionUtil;
import com.gr.censusmanagement.validation.ValidationService;
import com.gr.common.v2.constant.Constants;
import com.gr.common.v2.exception.model.ApiErrorDto;
import com.gr.logging.annotation.Loggable;

@Loggable
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = { Constants.Header.X_API_KEY })
@Secured(SecuredType.API_KEY)
@RestController
@RequestMapping("/api/external/v1")
public class SourceSystemController implements SourceSystemApi {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountPocService accountPocService;

	@Autowired
	private AuthService authService;

	@Autowired
	private ValidationService validationService;
	
	@Autowired
	private TrcmFormService trcmFormService;

	@Override
	@PostMapping("/{sourceAccountId}/account")
	public ResponseEntity<IdDto> syncAccount(@RequestHeader(Constants.Header.X_API_KEY) String apiKey, @PathVariable("sourceAccountId") String sourceAccountId,
			@RequestBody AccountDto accountDto) throws Exception {
		return ResponseEntity.ok(ProjectionUtil.toIdDto(accountService.saveOrUpdate(AccountMapper.toEntity(accountDto), apiKey)));
	}

	@Override
	@PostMapping("/{sourceAccountId}/poc")
	public ResponseEntity<IdDto> syncPoc(@PathVariable("sourceAccountId") String sourceAccountId, @RequestBody AccountPocDto accountPocDto) throws Exception {
		return ResponseEntity.ok(ProjectionUtil.toIdDto(accountPocService.saveOrUpdate(AccountPocMapper.toEntity(accountPocDto), sourceAccountId)));
	}

	@Override
	@PostMapping(path = "/silentloginforparticipant")
	public ResponseEntity<?> generateSilentLoginTokenForParicipant(@RequestBody GridContactSilentLoginRequestDto gridContactSilentLoginRequestDto) {
		return ResponseEntity.ok(new HashMap<String, String>() {
			{
				put("token", authService.generateSilentLoginTokenForParticipant(gridContactSilentLoginRequestDto));
			}
		});
	}
	
	@PostMapping(path = "/trcmformdata")
	public ResponseEntity<?> saveOrUpdateTrcmFormData(@Valid @RequestBody TrcmFormDataRequestDto trcmFormDataDto) {
		List<ApiErrorDto> apiErrors = validationService.validateTrcmFormDataRequest(trcmFormDataDto);
		if (CollectionUtils.isEmpty(apiErrors)) {
			trcmFormDataDto.setSource("grcom");
			return ResponseEntity.ok(ProjectionUtil.toIdDto(trcmFormService.saveOrUpdateTrcmFormData(TrcmFormDataMapper.toEntity(trcmFormDataDto))));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrors);
	}
	
	@GetMapping("/active/{accountId}")
	public ResponseEntity<TrcmFormDto> getActiveTrcmFormByAccountId(@PathVariable("accountId") String accountId) {
		return ResponseEntity.ok(trcmFormService.getActiveTrcmFormById(accountId));

	}

}
