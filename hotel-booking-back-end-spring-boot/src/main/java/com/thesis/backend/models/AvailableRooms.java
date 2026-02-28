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
@Table(name="AvailableRooms")
@Getter
@Setter
public class AvailableRooms {
	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private LocalDate fromDate ;
	@NotNull
	private LocalDate toDate ;

	@NotNull
	private String roomName ;
	
	@NotNull
	private String roomType ;
	
	@NotNull
	private int quantity ;
	
	@NotNull
	private int roomPrice ;
  
  
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
    private Hotel hotel;
 
}
