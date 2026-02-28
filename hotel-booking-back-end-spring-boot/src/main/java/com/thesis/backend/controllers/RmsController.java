package com.thesis.backend.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thesis.backend.models.Reservation;
import com.thesis.backend.payload.request.LoginRequest;
import com.thesis.backend.repository.ReservationRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rms")
public class RmsController {
	@Autowired
	public ReservationRepository reservationRepository;
	
	@GetMapping("/getReservations")
	public List<Reservation> getReservations(@Validated @RequestBody LoginRequest loginRequest)
	{
	
		
		return this.reservationRepository.findAll();
	}

}
