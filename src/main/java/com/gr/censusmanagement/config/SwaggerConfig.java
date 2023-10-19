package com.gr.censusmanagement.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gr.common.v2.constant.Constants;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
	@Bean
	public Docket externalControllerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.globalOperationParameters(getXApiKey())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.gr.censusmanagement.external.v1.controller"))
				.build()
				.groupName("ExternalController")
				.useDefaultResponseMessages(false)
				.ignoredParameterTypes()
				.apiInfo(getApiInfo());
	}
	
	@Bean
	public Docket icrmControllerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.gr.censusmanagement.internal.controller"))
				.build()
				.groupName("IcrmController")
				.useDefaultResponseMessages(false)
				.ignoredParameterTypes()
				.apiInfo(getApiInfo());
	}
	
	@Bean
	public Docket InternalControllerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.globalOperationParameters(getXAuthToken())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.gr.censusmanagement.v1.controller"))
				.build()
				.groupName("InternalController")
				.useDefaultResponseMessages(false)
				.ignoredParameterTypes()
				.apiInfo(getApiInfo());
	}
	
	private List<Parameter> getXApiKey() {
		Parameter apiKeyHeaderParam = new ParameterBuilder()
	            .name(Constants.Header.X_API_KEY)
	            .description("API key for authentication")
	            .modelRef(new ModelRef("string"))
	            .parameterType("header")
	            .required(true)
	            .build();
		
		return Arrays.asList(apiKeyHeaderParam);
	}
	
	private List<Parameter> getXAuthToken() {
		Parameter apiKeyHeaderParam = new ParameterBuilder()
	            .name(Constants.Header.X_AUTH_TOKEN)
	            .description("Auth token for authentication")
	            .modelRef(new ModelRef("string"))
	            .parameterType("header")
	            .required(true)
	            .build();
		
		return Arrays.asList(apiKeyHeaderParam);
	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder().description("").title("Census-Management").version("1.0").build();
	}
}
