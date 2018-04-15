package com.hpe.vapplocker.reservation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
public class Controller {
   
	// The requestor information
	static class Requestor {
		public String name;
	}
	
	public static final Logger logger = LoggerFactory.getLogger(Controller.class);
	
	ReservationService reservationService = new ReservationService();
	
	@RequestMapping(value = "/reservation/{domain}", method = RequestMethod.POST)
    public ResponseEntity<?> createReservation(@PathVariable("domain") String domain, @RequestBody Requestor requestor, 
    		UriComponentsBuilder ucBuilder) {
    	System.out.println(domain+": "+requestor.name);
        
    	int id = reservationService.reserve(domain, requestor.name);
    	if (id < 0) {
    		System.out.println("unable to reserve -- try again");
    		return new ResponseEntity<String>(HttpStatus.REQUEST_TIMEOUT);
    	}
    	else {
    		System.out.println("reserved");
	    	HttpHeaders headers = new HttpHeaders();
	        headers.setLocation(ucBuilder.path("/reservation/{domain}/{id}").buildAndExpand(domain, id).toUri());
	        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    	}
    }
	
	@RequestMapping(value = "/reservation/{domain}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservation(@PathVariable("domain") String domain, @PathVariable("id") int id) {
		reservationService.release(domain, id);
		return new ResponseEntity<String>(HttpStatus.OK); 
	}

}