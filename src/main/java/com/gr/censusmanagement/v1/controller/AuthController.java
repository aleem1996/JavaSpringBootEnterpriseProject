package com.gr.censusmanagement.v1.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gr.auth.annotation.Secured;
import com.gr.censusmanagement.service.AuthService;
import com.gr.common.v2.constant.Constants;
import com.gr.logging.annotation.Loggable;

@Loggable
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = { Constants.Header.X_AUTH_TOKEN })
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@GetMapping
	public ResponseEntity<Void> validateToken(@RequestHeader(Constants.Header.X_AUTH_TOKEN) String token, HttpServletResponse httpServletResponse) throws ParseException {
		httpServletResponse.addHeader(Constants.Header.X_AUTH_TOKEN, authService.generateFinalToken(token));
		return ResponseEntity.ok(null);
	}
	
	@GetMapping("/validate-token-dynamics")
	public ResponseEntity<Void> validateTokenDynamics(@RequestHeader(Constants.Header.X_AUTH_TOKEN) String token, HttpServletResponse httpServletResponse) throws ParseException {
		httpServletResponse.addHeader(Constants.Header.X_AUTH_TOKEN, authService.generateFinalTokenDynamics(token));
		return ResponseEntity.ok(null);
	}
	
	@Secured
	@GetMapping("/refreshtoken")
	public ResponseEntity<Void> refreshToken() {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@GetMapping("/healthcheck")
	public ResponseEntity<Void> healthCheck() {
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
