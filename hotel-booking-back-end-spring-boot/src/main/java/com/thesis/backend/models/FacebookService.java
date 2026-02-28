package com.thesis.backend.models;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagePostData;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;

import com.thesis.backend.repository.AvailabilityRepository;
import com.thesis.backend.repository.HotelRepository;
import com.thesis.backend.repository.ImageRepository;

@Service
public class FacebookService {
	@Value("${spring.social.facebook.appId}")
	String facebookAppId;
	@Value("${spring.social.facebook.appSecret}")
	String facebookSecret;

	String accessToken = "EAAvRY63BlEgBAMwJvFirU11xQPGwEQQ69K4nZAlkmpwMWbW0bOx2BZBZCg1gh94FI8eXmK00vv97QysYoRRUxWuGTVeWJo9FiCt49Gt6WVBdQVXuFTm8jnQ66ZB0onATxvvWpRDlS9AbLG4JJXlGVc9Ex9tzhZC6O0B5MMVTZBZBG7pX7yo6X0O";

	@Autowired
	HotelRepository hotelRepository;

	@Autowired
	ImageRepository imageRepository;

	@Autowired
	AvailabilityRepository availabilityRepository;



	// This method is scheduled to execute one time every day
	//@Scheduled(fixedRate = 86400000)
	public String PostToPage() throws IOException {

		// Create an LocalDate object to retrieve current date
		LocalDate lt = LocalDate.now();
		// Print result
		System.out.println("LocalDate : " + lt);

		// I need to retrieve availabilities for current date
		List<Availability> availabilities = new ArrayList<Availability>();
		availabilities = this.availabilityRepository.findAllByDate(lt);
		System.out.println("size of availabilities arraylist is " + availabilities.size());

		// if availability is 0 i remove it
		if (availabilities.size() > 0) {
			for (int i = 0; i < availabilities.size(); i++) {
				if (availabilities.get(i).getAvailable() == 0) {
					availabilities.remove(i);

				}
			}
		}
		// now i need to find min availability to make the advertisement
		Availability availabilityWithMinRoomPrice = new Availability();
		int minRoomPrice = 0;
		if (availabilities.size() > 0) {
			minRoomPrice = availabilities.get(0).getRoomPrice();
			availabilityWithMinRoomPrice = availabilities.get(0);
			for (int i = 0; i < availabilities.size(); i++) {

				if (availabilities.get(i).getRoomPrice() < minRoomPrice) {
					minRoomPrice = availabilities.get(i).getRoomPrice();
					availabilityWithMinRoomPrice = availabilities.get(i);
				}
			}
		}

		System.out.println("size of availabilities arraylist is " + availabilities.size());
		System.out.println("The id of min availability " + availabilityWithMinRoomPrice.getId());
		// We need to retrieve the images for the selected hotel with min value
		List<Image> images = new ArrayList<Image>();
		images = this.imageRepository.findAllByHotelId(availabilityWithMinRoomPrice.getHotel().getId());
		System.out.println("size of image arraylist is " + images.size());

		// I do some tricks to be able to take the image as a resource
		ByteArrayInputStream bis = new ByteArrayInputStream(images.get(0).getData());
		BufferedImage bImage2 = ImageIO.read(bis);
		ImageIO.write(bImage2, "jpg", new File("output.jpg"));
		System.out.println("image created");
		Resource resource = null;

		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X
		// button to close the app
		resource = new FileSystemResource("output.jpg");
		// Image image=new Image(hotels.get(0).getHotelImages().get(0).getData());

		// Now i need to post to Facebook page
		Facebook facebook = new FacebookTemplate(accessToken);
		return facebook.pageOperations().postPhoto("103702075987548", "103702075987548", resource,
				"Available rooms in " + availabilityWithMinRoomPrice.getHotel().getHotelName() + " hotel starting from "
						+ availabilityWithMinRoomPrice.getRoomPrice() + "$. Check other hotels in "
						+ availabilityWithMinRoomPrice.getHotel().getAddress().getCity() + " www.example.com");
		// return "ok";
	}

}
