package com.thesis.backend.payload.request;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

import com.thesis.backend.models.HotelAddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelInfoRequest {
	@NotNull
	private String hotelName;
	
	@Lob
	private String hotelDescription;
	
	@NotNull
	private HotelAddress address;
}
