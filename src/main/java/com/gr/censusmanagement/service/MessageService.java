package com.gr.censusmanagement.service;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	@Autowired
	private Environment env;

	public String getErrorMessage(String code) {
		return env.getProperty("common.error." + code);
	}

	public String getFormattedErrorMessage(String code, Object... params) {
		return MessageFormat.format(env.getProperty("common.error." + code), params);
	}
}
