package com.thesis.backend.controllers;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thesis.backend.models.User;
import com.thesis.backend.repository.UserRepository;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getUsers( ) {
	
        return userRepository.findAll();

    }
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId ) {
	
        try {
         userRepository.deleteById(userId);
         return ResponseEntity.ok("user deleted succesfully");
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.notFound().build() ;//400
        }
        

    }
    
}
