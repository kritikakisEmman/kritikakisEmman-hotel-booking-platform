package com.thesis.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thesis.backend.models.Availability;
import com.thesis.backend.models.Hotel;
import com.thesis.backend.models.Reservation;
import com.thesis.backend.models.ReservationType;
import com.thesis.backend.payload.request.ReservationRequest;
import com.thesis.backend.payload.response.MessageResponse;
import com.thesis.backend.repository.HotelRepository;
import com.thesis.backend.repository.ReservationRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
@Autowired
private ReservationRepository reservationRepository;

@Autowired
private HotelRepository hotelRepository;
	
@PostMapping("/setReservation")
public ResponseEntity<?> setReservation(@RequestBody ReservationRequest reservationRequest) {
	Reservation reservation=new Reservation();
	Availability availability=new Availability();
	Hotel hotel=new Hotel();
	//we used reservationRequest object instead of reservation object because we need to set hotel value.
	//thats why we need to pupulate the real reservation object now by hard way
	reservation.setFirstName(reservationRequest.getFirstName());
	reservation.setLastName(reservationRequest.getLastName());
	reservation.setPhoneNumber(reservationRequest.getPhoneNumber());
	reservation.setEmail(reservationRequest.getEmail());
    reservation.setFromDate(reservationRequest.getFromDate());
    reservation.setToDate(reservationRequest.getToDate());
   
    for(int i=0;i<reservationRequest.getReservationTypes().size();i++)
    {
    	reservationRequest.getReservationTypes().get(i).setReservation(reservation);
    	
    }
    reservation.setReservationTypes(reservationRequest.getReservationTypes());
    
    hotel=this.hotelRepository.getById(reservationRequest.getHotelId());
    //also we need to change the availability array for this hotel
    System.out.println("to availability.date = "+ availability.getDate());
	System.out.println("to reservation.getFromDate = "+ reservation.getFromDate());
	System.out.println("to availability.getToDate = "+ reservation.getToDate());
    for(int i=0;i<hotel.getHotelAvailabilities().size();i++)
    {
    	availability=hotel.getHotelAvailabilities().get(i);
    	
    	if((availability.getDate().isAfter(reservation.getFromDate())||availability.getDate().equals(reservation.getFromDate()))&&availability.getDate().isBefore(reservation.getToDate())) {
    		for(ReservationType reservationType: reservation.getReservationTypes()) {
    			if(availability.getType().equals(reservationType.getRoomType())&&availability.getRoomName().equals(reservationType.getRoomName()))
    				hotel.getHotelAvailabilities().get(i).setAvailable(availability.getAvailable()-reservationType.getQuantity());
    			
    			
    		}
    	}
    }
    
    
    reservation.setHotel(hotel);
   //now that we populate all values correctly we need to save the item to database
	reservationRepository.save(reservation);
	
	
	
	
    return ResponseEntity.ok(new MessageResponse("Reservation added successfully!"));
 
    
}

@DeleteMapping("/deleteReservation/{hotelId}/{reservationId}")
public void deleteReservation(@PathVariable long hotelId,@PathVariable long reservationId ) {

	  
	Reservation reservation=new Reservation();
	Availability availability=new Availability();
	System.out.println(hotelId);
	System.out.println(reservationId);
	
	Hotel hotel=this.hotelRepository.findById(hotelId).orElse(null); 
	
	for(int i=0;i<hotel.getHotelReservations().size();i++)
	{
		System.out.println(hotel.getHotelReservations().get(i).getId());
		if(hotel.getHotelReservations().get(i).getId()==reservationId) {
			reservation=hotel.getHotelReservations().get(i);
			hotel.getHotelReservations().remove(i);
			
		}
		
		
	}
	
    for(int i=0;i<hotel.getHotelAvailabilities().size();i++)
    {
    	availability=hotel.getHotelAvailabilities().get(i);
    	
    	if((availability.getDate().isAfter(reservation.getFromDate())||availability.getDate().equals(reservation.getFromDate()))&&availability.getDate().isBefore(reservation.getToDate())) {
    		for(ReservationType reservationType: reservation.getReservationTypes()) {
    			if(availability.getType().equals(reservationType.getRoomType())&&availability.getRoomName().equals(reservationType.getRoomName()))
    				hotel.getHotelAvailabilities().get(i).setAvailable(availability.getAvailable()+reservationType.getQuantity());
    			
    			
    		}
    	}
    }
	
	this.hotelRepository.save(hotel);
	
} 


}