package com.gr.censusmanagement.controller.advice;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.censusmanagement.external.model.FailureResponseDto;
import com.gr.common.v2.constant.Constants;
import com.gr.common.v2.exception.model.ApiErrorDto;
import com.gr.common.v2.service.JsonService;
import com.gr.integration.notification.dto.request.EmailSendRequestDto;
import com.gr.integration.notification.dto.request.EmailSendRequestDto.ProfileRecipientType;
import com.gr.integration.notification.service.GrNotificationIntegrationService;
import com.gr.logging.service.LogBuilderService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice("com.gr.censusmanagement")
public class ErrorHandlingControllerAdvice {

	private final Configuration templateConfiguration;

	@Autowired
	private Environment env;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private JsonService jsonService;

	@Autowired
	private LogBuilderService logBuilderService;

	@Autowired
	private GrNotificationIntegrationService grNotificationIntegrationService;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${app.email.templates.location}")
	private String basePackagePath;

	@Value("${app.email.to.supportDistro}")
	private String supportDistro;

	@Autowired
	public ErrorHandlingControllerAdvice(Configuration templateConfiguration) {
		super();
		this.templateConfiguration = templateConfiguration;
	}

	private static String DATE_FORMAT = "EEEE, MMMM dd, yyyy hh:mm:ss a";

	@ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, Exception.class })
	public ResponseEntity<?> handleException(Exception ex, WebRequest request) {
		log.error("Exception", ex);
		ResponseEntity<?> response = null;
		String subject = ex.getClass().getSimpleName();

		if (ex instanceof MethodArgumentNotValidException) {
			response = handleValidationExceptions(convert(ex, MethodArgumentNotValidException.class));
			subject = "Request Validation Failed";
		} else if (ex instanceof HttpMessageNotReadableException) {
			response = handleHttpMessageNotReadableExceptionExceptions(convert(ex, HttpMessageNotReadableException.class));

			if (ExceptionUtils.indexOfThrowable(ex, InvalidFormatException.class) > -1) {
				subject = "Request Validation Failed";
			} else {
				subject = "Request Malformed";
			}

		} else {
			response = defaultHandler(ex);
			subject = ex.getClass().getSimpleName();
		}

		sendExceptionEmail(subject, ex, response.getBody());
		return response;

	}

	private ResponseEntity<List<ApiErrorDto>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		FailureResponseDto failureResponse = new FailureResponseDto();
		Map<String, ApiErrorDto> errorMap = new HashMap<String, ApiErrorDto>();

		ex.getBindingResult().getAllErrors().forEach((error) -> {
			if (error instanceof FieldError) {
				String field = ((FieldError) error).getField();
				String code = error.getDefaultMessage();

				boolean isSizeValidation = false;

				// check if validation is of size on field
				Object[] ObjectList = error.getArguments();
				Integer max = 0;
				Integer min = 0;
				for (Object obj : ObjectList) {
					if (obj.getClass().equals(Integer.class)) {

						if ((Integer) obj > max) {
							max = (Integer) obj;
							isSizeValidation = true;
						}

						if ((Integer) obj < max) {
							min = (Integer) obj;
							isSizeValidation = true;
						}
					}
				}

				String message = null;
				if (isSizeValidation) {
					if (ErrorCodes.BR_CAS_217.equals(code)) {
						message = MessageFormat.format(env.getProperty("common.error." + code), field, min, max);
					} else if (ErrorCodes.BR_CAS_210.equals(code)) {
						message = MessageFormat.format(env.getProperty("common.error." + code), field, max);
					} else {
						message = MessageFormat.format(env.getProperty("common.error." + code), field);
					}
				} else {
					message = MessageFormat.format(env.getProperty("common.error." + code), field);
				}

				ApiErrorDto dataError = new ApiErrorDto(field, code, message);
				if (errorMap.containsKey(field)) {
					if (ErrorCodes.BR_CAS_200.equals(dataError.getCode())) {
						errorMap.put(field, dataError);
					} else if ((ErrorCodes.BR_CAS_210.equals(dataError.getCode()) || ErrorCodes.BR_CAS_217.equals(dataError.getCode()))
							&& !isEmptyAlreadyChecked(errorMap, field, ErrorCodes.BR_CAS_200)) {
						errorMap.put(field, dataError);
					} else if (ErrorCodes.BR_CAS_103.equals(dataError.getCode()) && !isEmptyAlreadyChecked(errorMap, field, ErrorCodes.BR_CAS_200)) {
						errorMap.put(field, dataError);
					}
				} else {
					errorMap.put(field, dataError);
				}

			} else if (error instanceof ObjectError) {
				handleCustomValidationExeception(failureResponse, error);
			}
		});

		errorMap.forEach((key, value) -> failureResponse.getErrors().add(value));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse.getErrors());
	}

	public ResponseEntity<?> handleHttpMessageNotReadableExceptionExceptions(HttpMessageNotReadableException ex) {
		if (ExceptionUtils.indexOfThrowable(ex, UnrecognizedPropertyException.class) > -1) {
			UnrecognizedPropertyException urpex = (UnrecognizedPropertyException) ex.getCause();
			String code = ErrorCodes.BR_CAS_501;
			String message = MessageFormat.format(env.getProperty("common.error." + code), urpex.getPropertyName());
			FailureResponseDto failureResponse = new FailureResponseDto(new ApiErrorDto(urpex.getPropertyName(), code, message));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse.getErrors());

		} else if (ExceptionUtils.indexOfThrowable(ex, InvalidFormatException.class) > -1) {
			InvalidFormatException inv = (InvalidFormatException) ex.getCause();
			String code = ErrorCodes.BR_CAS_202;
			String field = "";

			for (int i = 0; i < inv.getPath().size(); i++) {
				Reference path = inv.getPath().get(i);
				if (i == inv.getPath().size() - 1) {
					field += path.getFieldName();
				} else {
					if (path.getFrom() instanceof Collection) {
						field += path.getIndex() + ".";
					} else {
						field += path.getFieldName() + ".";
					}
				}
			}

			String message = MessageFormat.format(env.getProperty("common.error." + code), field);
			FailureResponseDto failureResponse = new FailureResponseDto(new ApiErrorDto(field, code, message));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse.getErrors());

		} else {
			String code = ErrorCodes.BR_CAS_502;
			String message = env.getProperty("common.error." + code);
			FailureResponseDto failureResponseDto = new FailureResponseDto(new ApiErrorDto(null, code, message));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponseDto.getErrors());
		}
	}

	private boolean isEmptyAlreadyChecked(Map<String, ApiErrorDto> errorMap, String field, String errorCode) {
		if (errorMap.containsKey(field)) {
			ApiErrorDto apiError = errorMap.get(field);

			if (apiError.getCode().equals(errorCode)) {
				return true;
			}
		}
		return false;
	}

	private void handleCustomValidationExeception(FailureResponseDto failureResponse, ObjectError error) {
		ObjectError objectError = (ObjectError) error;
		String field = "";
		String code = "";

		for (Object arg : objectError.getArguments()) {
			if (!(arg instanceof DefaultMessageSourceResolvable)) {
				String str = arg.toString();
				if (str.contains("BR-CAS")) {
					code = str;
				} else {
					field = str;
				}
			}
		}
	}

	private void sendExceptionEmail(String subject, Exception ex, Object response) {
		String grRequestId = (String) httpServletRequest.getAttribute(Constants.Header.X_GR_REQUEST_ID);
		String responseJson = jsonService.getAsJsonPrettyString(response);
		subject = applicationName + " - " + subject;

		templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
		Map<String, Object> mappings = new HashMap<>();
		String emailBody = null;
		EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.ERROR);
//
		try {
			mappings.put("time", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
			mappings.put("uri", httpServletRequest.getRequestURI());
			mappings.put("grRequestId", grRequestId);
			mappings.put("httpMethod", httpServletRequest.getMethod());
			mappings.put("requestHeaders", logBuilderService.getHeadersString(httpServletRequest));
			mappings.put("responseJson", responseJson);
			mappings.put("stackTrace", ExceptionUtils.getStackTrace(ex));

			Template template = templateConfiguration.getTemplate("exception-email.html");
			emailBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, mappings);
			emailSendRequestDto.setEmailBody(emailBody);
			emailSendRequestDto.setSubject(subject);
			emailSendRequestDto.setEmailTo(supportDistro);
			grNotificationIntegrationService.sendEmailByProfileRecipientType(ProfileRecipientType.ERROR, emailSendRequestDto);
		} catch (IOException | TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResponseEntity<Void> defaultHandler(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@SuppressWarnings("unchecked")
	private <T extends Exception> T convert(Exception ex, Class<T> clazz) {
		return (T) ex;
	}

}

