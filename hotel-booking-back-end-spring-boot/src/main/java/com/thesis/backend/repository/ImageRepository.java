package com.thesis.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thesis.backend.models.Image;

public interface ImageRepository extends JpaRepository<Image,Long>{

	List<Image> findAllByHotelId(Long id);

}
