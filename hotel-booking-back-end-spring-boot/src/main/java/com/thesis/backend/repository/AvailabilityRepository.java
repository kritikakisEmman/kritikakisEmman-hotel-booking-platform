package com.thesis.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thesis.backend.models.Availability;



public interface AvailabilityRepository  extends JpaRepository<Availability,Long>{

	List<Availability> findAllByDate(LocalDate lt);

}
