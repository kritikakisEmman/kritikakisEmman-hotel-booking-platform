package com.thesis.backend.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
	@Lob
	private byte[] data;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
    private Hotel hotel; 
	

public Image(byte[] data)
{
	this.data=data;
}
public Image()
{
	
}
}
