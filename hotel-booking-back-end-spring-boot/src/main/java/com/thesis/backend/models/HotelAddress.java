package com.thesis.backend.models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="HotelAddress")
@Getter
@Setter 
public class HotelAddress {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
   

	@NotNull 
	private String street;
	@NotNull 
	private String city;
	@NotNull  
	private String state;
	@NotNull 
	private Integer zipCode;
	
	 @OneToOne(cascade = CascadeType.ALL, mappedBy = "address", fetch = FetchType.LAZY)
	 @JsonBackReference
	 private Hotel hotel;
	
	
}
