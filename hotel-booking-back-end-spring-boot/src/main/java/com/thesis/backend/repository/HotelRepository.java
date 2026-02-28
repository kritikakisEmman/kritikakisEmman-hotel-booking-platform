package com.thesis.backend.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.thesis.backend.models.Hotel;
public interface HotelRepository extends JpaRepository<Hotel,Long>  {

	List<Hotel> findAllByAddress_City(String location);

	Optional<Hotel> findAllByUserId(long userId);
    
	

	


	

	

}
