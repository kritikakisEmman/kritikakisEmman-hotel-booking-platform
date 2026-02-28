package com.thesis.backend.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name="availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

  
    @NotNull
    private LocalDate date;
    
    
    @NotNull
    private String type;

    @NotNull
    private String roomName;
    @NotNull
    private int available;
    
    @NotNull
    private int roomPrice;
    
	@ManyToOne
	@JsonBackReference
    private Hotel hotel;

}
