package com.gr.censusmanagement.v1.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gr.auth.annotation.Secured;
import com.gr.auth.annotation.SecuredType;
import com.gr.censusmanagement.constant.CensusConstants;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.model.mapper.TrcmFormMapper;
import com.gr.censusmanagement.model.response.IdDto;
import com.gr.censusmanagement.service.TrcmFormService;
import com.gr.censusmanagement.util.ProjectionUtil;
import com.gr.common.v2.constant.Constants;
import com.gr.logging.annotation.Loggable;

@Loggable
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = { Constants.Header.X_AUTH_TOKEN })
@Secured
@RestController
@RequestMapping("/api/v1/trcmform")
public class TrcmFormController {

	@Autowired
	private TrcmFormService trcmFormService;

	@GetMapping("/{accountId}")
	public ResponseEntity<List<TrcmFormDto>> getTrcmFormByAccountId(@PathVariable("accountId") String accountId) {
		return ResponseEntity.ok(trcmFormService.getTrcmFormByAccountId(accountId));

	}

	@GetMapping("/active/{accountId}")
	public ResponseEntity<TrcmFormDto> getActiveTrcmFormByAccountId(@PathVariable("accountId") String accountId) {
		return ResponseEntity.ok(trcmFormService.getActiveTrcmFormById(accountId));

	}

	@PostMapping
	public ResponseEntity<IdDto> saveOrUpdateTrcmForm(@Valid @RequestBody TrcmFormDto trcmFormDto) {
		return ResponseEntity.ok(ProjectionUtil.toIdDto(trcmFormService.saveOrUpdateTrcmForm(TrcmFormMapper.toEntity(trcmFormDto))));
	}


}
