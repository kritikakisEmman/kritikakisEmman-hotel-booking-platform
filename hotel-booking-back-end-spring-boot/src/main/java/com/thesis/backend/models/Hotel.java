package com.thesis.backend.models;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.*;
 
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;



@Entity
@Table(name="Hotel")
@Getter
@Setter
public class Hotel {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
	@NotNull
	private String hotelName ;


	@Lob
	@Column(name="HOTEL_DESCRIPTION", length=512)
	private String hotelDescription;
	     
	@NotNull
	@OneToOne(cascade = CascadeType.ALL)
	@JsonManagedReference
    @JoinColumn(name = "address_id", referencedColumnName = "id")
	private HotelAddress address ;
	
	@NotNull
	@OneToMany(mappedBy="hotel",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
    private List<AvailableRooms> availableRooms = new ArrayList<>();
	
	
	@OneToMany(mappedBy="hotel",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
    private List<HotelService> hotelServices = new ArrayList<>();
    
	
	@OneToMany(mappedBy="hotel",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
    private List<Image> hotelImages = new ArrayList<>();
    
	@OneToMany(mappedBy="hotel",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
    private List<Availability> hotelAvailabilities  = new ArrayList<>();
	 
	@OneToMany(mappedBy="hotel",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
    private List<Reservation> hotelReservations  = new ArrayList<>();
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user ;

	
	
	   
}
