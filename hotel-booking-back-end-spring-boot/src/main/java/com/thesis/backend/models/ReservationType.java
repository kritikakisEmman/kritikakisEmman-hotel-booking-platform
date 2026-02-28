package com.thesis.backend.models;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="reservationType")
@Getter
@Setter
public class ReservationType {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @NotNull
	private int quantity ; 
    
	@NotNull
	private String roomType ;
	
	@NotNull
	private String roomName ;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
    private Reservation reservation; 
}
