package com.gr.censusmanagement.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.gr.censusmanagement.common.integration.GridClientService;
import com.gr.censusmanagement.external.model.ContactUsDto;
import com.gr.censusmanagement.util.Util;
import com.gr.integration.notification.dto.request.EmailSendRequestDto;
import com.gr.integration.notification.dto.request.EmailSendRequestDto.ProfileRecipientType;
import com.gr.integration.notification.service.GrNotificationIntegrationService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailSendService {

	@Autowired
	private GrNotificationIntegrationService grNotificationIntegrationService;

	@Autowired
	BulkSessionService bulkSessionService;

	private final Configuration templateConfiguration;

	@Value("${app.email.to.devDistro}")
	private String devDistro;

	@Value("${app.email.templates.location}")
	private String basePackagePath;
	
	@Value("${app.fileupload.path}")
	private String path;
	
	@Autowired
	private GridClientService gridClientService;


	@Autowired
	public EmailSendService(JavaMailSender mailSender, Configuration templateConfiguration) {
		this.templateConfiguration = templateConfiguration;
	}

	public void sendContactUsEmailToMS(ContactUsDto contactUsDto, String identifier) {
		log.info("sendContactUsEmailToMS method starts");

		Map<String, Object> mappings = new HashMap<>();
		EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.SUPPORT);
		mappings.put("messageTitle", contactUsDto.getTitle());
		mappings.put("message", contactUsDto.getMessage());
		mappings.put("partnerName", contactUsDto.getPartnerName());
		mappings.put("phoneNumber", contactUsDto.getPhoneNumber());
		mappings.put("accountName", contactUsDto.getAccountName());
		mappings.put("email", contactUsDto.getPartnerEmail());
		mappings.put("emailIsAbout", contactUsDto.getEmailIsAbout());
		mappings.put("timeZone", contactUsDto.getTimeZone());

		sendEmail("Census Management - Contact Us : " + contactUsDto.getRegardsOf() + " " + contactUsDto.getPartnerName(), devDistro, "contactUsAffinityPartners.html", mappings,
				emailSendRequestDto, identifier);
		log.info("sendContactUsEmailToMS method ends");
	}

	public void sendContactUsEmailToAffinityPartner(ContactUsDto contactUsDto, String identifier) {
		log.info("sendContactUsEmailToAffinityPartner() method starts");

		Map<String, Object> mappings = new HashMap<>();
		EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.SUPPORT);
		mappings.put("message", contactUsDto.getMessage());
		mappings.put("partnerName", contactUsDto.getPartnerName());
		sendEmail("Contact us message received", contactUsDto.getPartnerEmail(), "contactUsToAffinityPartner.html", mappings, emailSendRequestDto, identifier);
		log.info("sendContactUsEmailToAffinityPartner() method ends");
	}
	
	public void sendContactUsEmailForGrid(ContactUsDto contactUsDto, String identifier, String accountManagerEmail) {
		log.info("sendContactUsEmailForGrid method starts");

		Map<String, Object> mappings = new HashMap<>();
		EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.SUPPORT);
		mappings.put("messageTitle", contactUsDto.getTitle());
		mappings.put("message", contactUsDto.getMessage());
		mappings.put("partnerName", contactUsDto.getPartnerName());
		mappings.put("phoneNumber", contactUsDto.getPhoneNumber());
		mappings.put("accountName", contactUsDto.getAccountName());
		mappings.put("email", contactUsDto.getPartnerEmail());
		
		sendEmail("Census Management - Contact Us : " + contactUsDto.getRegardsOf() + " " + contactUsDto.getPartnerName(), accountManagerEmail, "Contact_Us_Message_Received.html", mappings,
				emailSendRequestDto, identifier);
		log.info("sendContactUsEmailForGrid method ends");
	}

	public void sendBulkUploadProcessReportEmail(UUID sessionId, String statusReportPath, String accountSource, String accountId) {
		log.info("sendBulkUploadProcessReportEmail method starts");

		Map<String, Object> mappings = new HashMap<>();
		EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.END_USER);

		if ("grid".equalsIgnoreCase(accountSource)) {
			Boolean isWhiteList = gridClientService.getIsWhiteListEmailAccount(accountId);
			emailSendRequestDto.setIsWhiteListEmail(isWhiteList);
		} else {
			emailSendRequestDto.setIsWhiteListEmail(Boolean.TRUE);
		}
		
		statusReportPath = path + statusReportPath;
		emailSendRequestDto.setAttachments(Arrays.asList(statusReportPath));
		mappings.put("accountName", bulkSessionService.getSession(sessionId).getAccountName());
		String successRow = "<li style='margin-top:10px;'><strong>Successful Record(s): </strong>" + bulkSessionService.getSession(sessionId).getSuccessfulRecords() + "</li>";
		mappings.put("successfulTrcmRecords", successRow);
		String failureRow = "<li style='margin-top:10px;'><strong>Failed Record(s): </strong>" + bulkSessionService.getSession(sessionId).getUnSuccessfulRecords() + "</li>";
		mappings.put("unSuccessfulTrcmRecords", failureRow);
		mappings.put("requestedBy", bulkSessionService.getSession(sessionId).getRequestedBy());
		mappings.put("requestedOn", Util.getCommDetailFormatedDateTime(new Date()) + " (EST)");
		sendEmail("Trcm Form Records Status", bulkSessionService.getSession(sessionId).getUploaderEmail(), "trcmFormDataTemplate.html", mappings, emailSendRequestDto, sessionId.toString());
	}
	
	public void sendTheWorldEmail(String firstName, String emailTemplateFileName, String email, String accountId) {
		log.info("sendTheWorldEmail method starts");

		Map<String, Object> mappings = new HashMap<>();
		EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.END_USER);
		mappings.put("memberName", firstName);
		
		Boolean isWhiteList = gridClientService.getIsWhiteListEmailAccount(accountId);
		emailSendRequestDto.setIsWhiteListEmail(isWhiteList);
		
		// safe check
		if (Util.isNotNullAndEmpty(email)) {
			sendEmail("Welcome to Global Rescue - The World", email, emailTemplateFileName, mappings, emailSendRequestDto, null);			
		}

	}
	
	public Boolean sendExportEmail(String statusReportPath, String accountName, String uploaderEmail, String sourceName, String accountId) {
		log.info("sendExportEmail method starts");
		
		Map<String, Object> mappings = new HashMap<>();
		mappings.put("accountName", accountName);
		EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.END_USER);
		statusReportPath = path + statusReportPath;
		emailSendRequestDto.setAttachments(Arrays.asList(statusReportPath));
		String templateName = "";
		if ("grid".equalsIgnoreCase(sourceName)) {
			templateName = "Exported_Traveler_Records.html";
			Boolean isWhiteList = gridClientService.getIsWhiteListEmailAccount(accountId);
			emailSendRequestDto.setIsWhiteListEmail(isWhiteList);
		} else {
			templateName = "exportTravelerRecords.html";
			emailSendRequestDto.setIsWhiteListEmail(Boolean.TRUE);
		}
		return sendEmail(accountName + " - Exported Traveler Records", uploaderEmail, templateName, mappings, emailSendRequestDto, accountName);

	}

	private Boolean sendEmail(String subject, String toEmailAddresses, String templateFileName, Map<String, Object> mappings, EmailSendRequestDto emailSendRequestDto,
			String identifier) {
		try {
			templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
			if (Util.isNotNull(templateFileName) && Util.isNotNull(mappings)) {
				Template template = templateConfiguration.getTemplate(templateFileName);
				String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mappings);
				emailSendRequestDto.setEmailBody(mailContent);
			}
			emailSendRequestDto.setSubject(subject);
			emailSendRequestDto.setEmailTo(toEmailAddresses);
			return grNotificationIntegrationService.sendEmailByProfileRecipientType(emailSendRequestDto.getRecipientType(), emailSendRequestDto);
		} catch (Exception e) {
			log.error("Fail to email template for " + identifier, e);
		}
		return Boolean.FALSE;
	}

}
