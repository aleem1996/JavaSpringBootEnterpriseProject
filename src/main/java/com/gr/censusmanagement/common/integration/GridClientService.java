package com.gr.censusmanagement.common.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.gr.censusmanagement.constant.CensusConstants;
import com.gr.censusmanagement.integration.dto.GridTravelDataAckReqDto;
import com.gr.censusmanagement.integration.dto.TravelDataRequestDto;
import com.gr.censusmanagement.integration.dto.response.TravelDataDto;


@Service
public class GridClientService {

	@Autowired
	private RestTemplate gwsRestTemplate;

	@Value("${gridWsBaseUrl}")
	private String gridWsBaseUrl;
	
	@Value("${gridAccountServiceBaseUrl}")
	private String gridAccountServicBaseUrl;

	public TravelDataDto getTravelData(String key, String requestId, TravelDataRequestDto travelDataRequestDto) {
		
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(gridWsBaseUrl).build();
		HttpHeaders headers = buildHeader(key, requestId);

		HttpEntity<TravelDataRequestDto> requestEntity = new HttpEntity<>(travelDataRequestDto, headers);
		ResponseEntity<TravelDataDto> response = gwsRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, requestEntity, TravelDataDto.class);
		return response.getBody();
	}

	public void acknowledgeTravelData(String key, String requestId, GridTravelDataAckReqDto gridTravelDataAckReqDto) {
		
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(gridWsBaseUrl + CensusConstants.GRID_ACKNOWLEDGEMENT_STATUS).build();
		HttpHeaders headers = buildHeader(key, requestId);

		HttpEntity<GridTravelDataAckReqDto> requestEntity = new HttpEntity<>(gridTravelDataAckReqDto, headers);
		gwsRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, requestEntity, GridTravelDataAckReqDto.class);
	}
	
	private static HttpHeaders buildHeader(String key, String requestId) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("X_API_KEY", key);
		headers.set("X_REQUEST_ID", requestId);

		return headers;
	}
	
	private static HttpHeaders buildHeader(String requestId) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("X_REQUEST_ID", requestId);

		return headers;
	}
	
	public Boolean getIsWhiteListEmailAccount(String id) {
		try {
			UriComponents builder = UriComponentsBuilder.fromHttpUrl(gridAccountServicBaseUrl + id + "/is-whitelist-email").build();
			HttpHeaders headers = buildHeader("123");
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			ResponseEntity<Boolean> responseEntity = gwsRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, Boolean.class);
			return responseEntity.getBody();		
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
	}

}