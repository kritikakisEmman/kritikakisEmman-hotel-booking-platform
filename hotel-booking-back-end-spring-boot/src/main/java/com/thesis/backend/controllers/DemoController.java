package com.thesis.backend.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thesis.backend.models.FacebookService;



@RestController
@RequestMapping("/api/facebook")
public class DemoController {
	
	 @Autowired
	 FacebookService facebookService;

	
	 @GetMapping("/postToPage")
	 public String postToPage() throws IOException{
	     return facebookService.PostToPage();
	 }
}
