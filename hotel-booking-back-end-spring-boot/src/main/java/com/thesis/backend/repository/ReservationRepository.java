package com.thesis.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thesis.backend.models.Hotel;
import com.thesis.backend.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation,Long>{

}
