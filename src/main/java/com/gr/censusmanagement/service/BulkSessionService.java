package com.gr.censusmanagement.service;

import java.io.File;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gr.censusmanagement.external.model.BulkSessionDto;
import com.gr.censusmanagement.external.model.Status;
import com.gr.censusmanagement.util.Util;
import com.gr.censusmanagement.validation.ValidationService;

@Service
public class BulkSessionService {
	
	@Autowired
	ValidationService validationService;
	
	@Value("${app.fileupload.path}")
	private String path;
	
	private static final ConcurrentHashMap<UUID, BulkSessionDto> session = new ConcurrentHashMap<UUID, BulkSessionDto>();

	public UUID createSession() {

		UUID sessionId = UUID.randomUUID();

		while (session.containsKey(sessionId)) {
			sessionId = UUID.randomUUID();
		}

		session.putIfAbsent(sessionId, new BulkSessionDto());

		return sessionId;
	}
	
	public Boolean isSessionExist(UUID sessionId) {
		return Util.isNotNull(sessionId) && session.containsKey(sessionId);
	}
	
	public void createSession(UUID sessionId) {
		session.putIfAbsent(sessionId, new BulkSessionDto());
	}
	
	public BulkSessionDto getSession(UUID sessionId) {
		BulkSessionDto bulkSession = session.get(sessionId);

		if (bulkSession == null) {
			throw new IllegalArgumentException("Session does not exist for sessionId: " + sessionId);
		}
		return bulkSession;
	}
	
	private void markStatus(UUID sessionId, Status status) {
		getSession(sessionId).setStatus(status);
	}
	
	public void markAsInProgress(UUID sessionId) {
		markStatus(sessionId, Status.INPROGRESS);
	}
	
	public void updateProgress(UUID sessionId) {
		getSession(sessionId).updateProgress();
	}
	
	public void markAsComplete(UUID sessionId) {
		markStatus(sessionId, Status.SUCCESS);
	}
	
	public String getAccount(UUID sessionId) {
		return getSession(sessionId).getAccountId();
	}
	
	public void updateSuccessfulRecords(UUID sessionId) {
		getSession(sessionId).updateSuccessfulRecords();
	}
	
	public void updateUnSuccessfulRecords(UUID sessionId) {
		getSession(sessionId).updateUnSuccessfulRecords();
	}
	
	public void purgeSessions() {
		int days = 3;
		session.forEach((key, value) -> {
			if (Util.getDaysBetween(new Date(), value.getCreatedOn(), true) >= days) {
				String folderPath = path + key.toString();
				FileUtils.deleteQuietly(new File(folderPath));
				session.remove(key);
			}
		});
		
		deleteRemainingFiles(days, path);
	}
	
	private void deleteRemainingFiles(int days, String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		if(Util.isNull(listOfFiles)) {
			return;
		}

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isDirectory()) {

				Date createdDate = new Date(listOfFiles[i].lastModified());

				if (Util.getDaysBetween(new Date(), createdDate, true) >= days) {
					FileUtils.deleteQuietly(listOfFiles[i]);
				}
			}
		}
	}
}
