package com.gr.censusmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@PropertySources({ 
	@PropertySource("classpath:hibernate.properties"),
	@PropertySource("classpath:mail.properties"),
	@PropertySource("classpath:messages.properties"),
	@PropertySource("classpath:jwt.properties")
	})
public class ApplicationConfig {

}
