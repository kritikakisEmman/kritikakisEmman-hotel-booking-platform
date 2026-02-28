package com.thesis.backend.payload.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.thesis.backend.models.Hotel;
import com.thesis.backend.models.ReservationType;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReservationRequest {
	@NotNull
	private String firstName;
	
	@NotNull
	private String lastName;
	
	@NotNull
	private String phoneNumber; 
	
	@NotNull
	private String email; 
	
	@NotNull
	private LocalDate fromDate ;
	
	@NotNull
	private LocalDate toDate ;
	
	@NotNull
    private List<ReservationType> reservationTypes  = new ArrayList<>();
    
	@NotNull
	private Long hotelId ;
}
