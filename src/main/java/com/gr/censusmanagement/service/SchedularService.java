package com.gr.censusmanagement.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gr.censusmanagement.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SchedularService {

	@Autowired
	BulkSessionService bulkSessionService;

	@Autowired
	BaseMapTravelDataService baseMapTravelDataService;
	
	@Value("${app.fileupload.path}")
	private String path;
	
	@Scheduled(cron = "${census.scheduler.bulksession.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void purgeOldBulkSessions() {
		log.info("purgeOldBulkSessions method starts");
		bulkSessionService.purgeSessions();
		log.info("purgeOldBulkSessions method ends");
	}

	@Transactional
	@Scheduled(cron = "${census.scheduler.traveldata.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void getTravelData() throws ParseException {
		log.info("getTravelData method starts");
		baseMapTravelDataService.getTravelDataFromGrid();
		log.info("getTravelData method ends");
	}
	
	@Scheduled(cron = "${census.scheduler.exportfiles.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void purgeOldExportFiles() throws IOException {
		log.info("purgeOldExportFiles method starts");
		FileUtil.deleteOldExportFiles(path + "exportfiles");
		log.info("purgeOldExportFiles method ends");
	}

}
