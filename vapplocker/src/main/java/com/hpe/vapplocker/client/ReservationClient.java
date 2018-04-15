package com.hpe.vapplocker.client;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ReservationClient {

	String baseUrl;
	
	public ReservationClient(String hostAndPort)
	{
		this.baseUrl = "http://"+hostAndPort+"/reservation/";
	}
	
	public URI reserve(String domain, String name) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>("{\"name\":\""+name+"\"}",headers);
		URI response = restTemplate.postForLocation(baseUrl+domain, entity);
		System.out.println(response);
		return response;
	}
	
	public boolean release(String domain, String name, URI reservation) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete(reservation);
		return true;
	}

	// java -cp target/vapplocker-0.0.1-SNAPSHOT.jar -Dloader.main=com.hpe.vapplocker.client.ReservationClient org.springframework.boot.loader.PropertiesLauncher
	public static void main(String[] args) {
		ReservationClient rc = new ReservationClient("localhost:8080");
		try {
			URI reservation = rc.reserve("foo", "michael");
			rc.release("foo", "michael", reservation);
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.REQUEST_TIMEOUT) {
				System.out.println("*** timed out -- retry?");
			}
		}
	} 

}
