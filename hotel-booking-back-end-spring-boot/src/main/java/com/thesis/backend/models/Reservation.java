package com.thesis.backend.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter
@Setter
public class Reservation {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
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
	
	@OneToMany(mappedBy="reservation",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
    private List<ReservationType> reservationTypes  = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
    private Hotel hotel; 
}
