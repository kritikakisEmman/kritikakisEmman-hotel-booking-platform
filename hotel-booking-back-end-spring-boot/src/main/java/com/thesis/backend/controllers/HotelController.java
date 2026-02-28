package com.thesis.backend.controllers;

import com.thesis.backend.models.*;
import com.thesis.backend.payload.request.HotelInfoRequest;
import com.thesis.backend.payload.response.MessageResponse;
import com.thesis.backend.repository.HotelRepository;
import com.thesis.backend.repository.ImageRepository;
import com.thesis.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/hotel")
public class HotelController {
@Autowired
public HotelRepository hotelRepository;
@Autowired
public ImageRepository imageRepository;
@Autowired
public UserRepository userRepository;

@PostMapping("/setHotel")
public ResponseEntity<?> setHotel(@RequestPart("hotel") Hotel hotel,@RequestPart("files") MultipartFile[] files) {
	ArrayList<Image> images = new ArrayList<Image>(); // Create an ArrayList object
    Arrays.asList(files).stream().forEach(file -> {
    	try { 
    		 Image image=new Image(file.getBytes());
   		 images.add(image);
	     image.setHotel(hotel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    });
    hotel.setHotelImages(images);
    System.out.println(hotel.getUser().getUsername());
    hotel.setUser(userRepository.findByEmail(hotel.getUser().getEmail()));
    
    ArrayList<Availability> hotelAvailabilities = new ArrayList<Availability>(); // Create an ArrayList object
    Availability availability;
   
    for(int i=0;i<hotel.getAvailableRooms().size();i++)
    {
    	LocalDate end = hotel.getAvailableRooms().get(i).getToDate();
    	for (LocalDate date = hotel.getAvailableRooms().get(i).getFromDate(); date.isBefore(end); date = date.plusDays(1)) {
        availability = new Availability();

        availability.setDate(date);


     


        availability.setType(hotel.getAvailableRooms().get(i).getRoomType());
        
        availability.setAvailable(hotel.getAvailableRooms().get(i).getQuantity());
        availability.setRoomPrice(hotel.getAvailableRooms().get(i).getRoomPrice());
        availability.setRoomName(hotel.getAvailableRooms().get(i).getRoomName())   ;
        availability.setHotel(hotel);
        hotelAvailabilities.add(availability);
        
    	}
    }
    hotel.setHotelAvailabilities(hotelAvailabilities);
	this.hotelRepository.save(hotel);
	 
    return ResponseEntity.ok(new MessageResponse("Hotel added successfully!"));

}

@PostMapping("/setImage" )
public ResponseEntity<?> setImage(@RequestParam("files") MultipartFile[] files) {
	/*Image image=new Image();
	for(int i = 0;i<file.length;i++) {
	try {
		 image.setData(file[0].getBytes());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 this.imageRepository.save(image);
	 
	 
	}*/
	 String message = "";
	 try {
	      List<String> fileNames = new ArrayList<>();
	     
	      Arrays.asList(files).stream().forEach(file -> {
	    	try {
	    		
	    		 Image image=new Image(file.getBytes());
				this.imageRepository.save(image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        fileNames.add(file.getOriginalFilename());
	      });
	      message = "Uploaded the files successfully: " + fileNames;
	      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
	    } catch (Exception e) {
	      message = "Fail to upload files!";
	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
	    }

   // return ResponseEntity.ok(new MessageResponse("Hotel added successfully!"));

}
@GetMapping("/getHotelById/{hotelId}")
public Hotel getHotelById(@PathVariable long hotelId) {
	System.out.println(hotelId);
	Hotel hotel=new Hotel();
	hotel=this.hotelRepository.findById(hotelId).orElse(null);
	
	  return hotel;
   

}
@GetMapping("/getHotelByUserId/{userId}")
public Hotel getHotelByUserId(@PathVariable long userId ) {
	System.out.println(userId);
	Hotel hotel=new Hotel();
	hotel=this.hotelRepository.findAllByUserId(userId).orElse(null);

	  return hotel;

}
@PutMapping("/updateHotelInfo/{hotelId}")
public void updateHotelInfo(@PathVariable long hotelId,@RequestBody HotelInfoRequest hotelInfoRequest) {
	System.out.println(hotelId);
	System.out.println(hotelInfoRequest.getHotelName());
	System.out.println(hotelInfoRequest.getHotelDescription());
	System.out.println(hotelInfoRequest.getAddress().getStreet());
	System.out.println(hotelInfoRequest.getAddress().getCity());
	System.out.println(hotelInfoRequest.getAddress().getState());
	System.out.println(hotelInfoRequest.getAddress().getZipCode());
	
	Hotel hotel=this.hotelRepository.findById(hotelId).orElse(null);
	hotel.setHotelName(hotelInfoRequest.getHotelName());
	hotel.setHotelDescription(hotelInfoRequest.getHotelDescription());
	hotel.getAddress().setStreet(hotelInfoRequest.getAddress().getStreet());
	hotel.getAddress().setCity(hotelInfoRequest.getAddress().getCity());
	hotel.getAddress().setState(hotelInfoRequest.getAddress().getState());
	hotel.getAddress().setZipCode(hotelInfoRequest.getAddress().getZipCode());
	this.hotelRepository.save(hotel);
}
@PutMapping("/updateAvailableRooms/{hotelId}")
public void updateAvailableRooms(@PathVariable long hotelId,@RequestBody ArrayList<AvailableRooms> availableRooms) {
	System.out.println(hotelId);
	System.out.println(availableRooms.size());
	
	Hotel hotel=this.hotelRepository.findById(hotelId).orElse(null);
	for( AvailableRooms availableRoom:availableRooms)
	{
		availableRoom.setHotel(hotel);
		hotel.getAvailableRooms().add(availableRoom);
		
	}
	
	ArrayList<Availability> hotelAvailabilities = new ArrayList<Availability>(); // Create an ArrayList object
    Availability availability;
   
    for(int i=0;i<availableRooms.size();i++)
    {
    	LocalDate end = availableRooms.get(i).getToDate();
    	for (LocalDate date = availableRooms.get(i).getFromDate(); date.isBefore(end); date = date.plusDays(1)) {
    
        availability = new Availability();

        availability.setDate(date);


     


        availability.setType(availableRooms.get(i).getRoomType());
        
        availability.setAvailable(availableRooms.get(i).getQuantity());
        availability.setRoomPrice(availableRooms.get(i).getRoomPrice());
        availability.setRoomName(availableRooms.get(i).getRoomName());
        availability.setHotel(hotel);
        hotel.getHotelAvailabilities().add(availability);
      
    	}
    }
   
		this.hotelRepository.save(hotel);
	this.hotelRepository.save(hotel);
}

@GetMapping("/getHotels")
public List<Hotel> getHotels() {
	List<Hotel> hotels=new ArrayList<Hotel>();
	hotels=this.hotelRepository.findAll();
	return hotels;
   

}
@PutMapping("/updateHotelServices/{hotelId}")
public void updateHotelServices(@PathVariable long hotelId,@RequestBody ArrayList<HotelService> hotelServices) {

	

	
	Hotel hotel=this.hotelRepository.findById(hotelId).orElse(null); 
	
	hotel.getHotelServices().clear();
	this.hotelRepository.save(hotel);
	 for(int i=0;i<hotelServices.size();i++)
	    {
	   


	     
		 	HotelService hotelService=new HotelService();
		 	hotelService.setServiceName(hotelServices.get(i).getServiceName());
		 	hotelService.setHotel(hotel);
	        hotel.getHotelServices().add(hotelService);
	      
	    	
	    }
	this.hotelRepository.save(hotel);
	
}
@PutMapping("/updateHotelImages")
public void updateHotelImages(@RequestPart("hotelId") long hotelId,@RequestPart("files") MultipartFile[] files ) {

	  

	System.out.println(hotelId);
	System.out.println(files.length);
	
	Hotel hotel=this.hotelRepository.findById(hotelId).orElse(null); 
	ArrayList<Image> images = new ArrayList<Image>(); // Create an ArrayList object
    Arrays.asList(files).stream().forEach(file -> {
    	try { 
    		 Image image=new Image(file.getBytes());
   		
	     image.setHotel(hotel);
	     images.add(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    });
	hotel.getHotelServices().clear();
	this.hotelRepository.save(hotel);
	 for(int i=0;i<images.size();i++)
	    {
	   


	     
		 hotel.getHotelImages().add(images.get(i));
	      
	    	
	    }
	this.hotelRepository.save(hotel);
	
} 
@DeleteMapping("/deleteHotelImageById/{hotelId}/{hotelImageId}")
public void deleteHotelImageById(@PathVariable long hotelId,@PathVariable long hotelImageId ) {

	  

	System.out.println(hotelId);
	System.out.println(hotelImageId);
	
	Hotel hotel=this.hotelRepository.findById(hotelId).orElse(null); 
	
	
	
	 for(int i=0;i<hotel.getHotelImages().size();i++)
	    {
	   


	     if(hotel.getHotelImages().get(i).getId()==hotelImageId)
	    	 hotel.getHotelImages().remove(i);
	      
	    	
	    }
	this.hotelRepository.save(hotel);
	
} 
@GetMapping("/getHotelsPaginated")

public Page<Hotel> getPage(Pageable pageable)
{

    Page<Hotel> page = hotelRepository.findAll(pageable);
    System.out.println(page.getNumberOfElements());
    List<Hotel> newhotel  = new ArrayList<>();
    Hotel hotelRequest;
    for (int i = 0; i < page.getContent().size(); i++) {
    	hotelRequest = new Hotel();
    	hotelRequest.setId(page.getContent().get(i).getId());
    	hotelRequest.setHotelName(page.getContent().get(i).getHotelName());
    	hotelRequest.setHotelDescription(page.getContent().get(i).getHotelDescription());
    	hotelRequest.setAddress(page.getContent().get(i).getAddress());
    	hotelRequest.setHotelServices(page.getContent().get(i).getHotelServices());
    	hotelRequest.setHotelImages(page.getContent().get(i).getHotelImages());
    	hotelRequest.setAvailableRooms(page.getContent().get(i).getAvailableRooms());
    	newhotel.add(i,hotelRequest);
    }
    Page<Hotel> hotels = new PageImpl<>(newhotel, pageable,page.getTotalElements());

    return hotels;
}
//This method is called only if search for hotels with criteria is performed
@GetMapping("/getHotelsPaginatedWithSearchCriteria/{stringFromDate}/{stringToDate}/{location}/{numberOfPersons}/{numberOfRooms}")
public Page<Hotel> getPageWithSearchCriteria(Pageable pageable ,@PathVariable String stringFromDate,@PathVariable String stringToDate,@PathVariable String location,@PathVariable int numberOfPersons,@PathVariable int numberOfRooms)
{ 
	
	System.out.println("numberOfRooms is ->"+numberOfRooms);
	System.out.println("location received is -> '"+location+"'");
	LocalDate fromDate=LocalDate.parse(stringFromDate);
	LocalDate toDate=LocalDate.parse(stringToDate);
	System.out.println("pageable to string gives-> "+pageable.toString());
	System.out.println("fromDate is-> "+fromDate);
	System.out.println("toDate is-> "+toDate);
    List<Hotel> hotels = hotelRepository.findAllByAddress_City(location); 
    List<Hotel> hotelsAfterDateCheck = new ArrayList<>();
    List<Hotel> hotelsAfterAvailabilityCheck = new ArrayList<>();
    System.out.println("hotels size gives-> "+hotels.size());
    for (Hotel hotel : hotels)
    {
    	for (AvailableRooms availableRoom : hotel.getAvailableRooms())
    	{
    		LocalDate availableRoomFromDate=availableRoom.getFromDate();
    		LocalDate availableRoomToDate=availableRoom.getToDate();
    		if((fromDate.isAfter(availableRoomFromDate)||fromDate.equals(availableRoomFromDate))&&(toDate.isBefore(availableRoomToDate)||toDate.equals(availableRoomToDate))) {
    			
    			 System.out.println("this hotel matches ");
    			hotelsAfterDateCheck.add(hotel);
    			break; 
    		}else
    		{
    		 System.out.println("this hotel doesnt belong");
    		
    		}
    	} 
    	
    }
    System.out.println("size of hotel after date check is -> "+hotelsAfterDateCheck.size());
   
      
   
    for (Hotel hotel : hotelsAfterDateCheck)
    {
    	 boolean haveRoomsAvailable=false;
    	 List<Availability> availableTypes = new ArrayList<>();
    	System.out.println("i am checking availability for hotel"+hotel.getHotelName());
        String failName= new String("");
    	String tempName="";
    	LocalDate tempDate= LocalDate.parse(stringFromDate);
    	int sum=0;
    	 
    	for (Availability availability : hotel.getHotelAvailabilities())
    	{
    		System.out.println("index id is"+availability.getId());
    		if((availability.getDate().isAfter(fromDate)||availability.getDate().equals(fromDate))&&availability.getDate().isBefore(toDate))
    		{
    			
    			 
    			if(availability.getAvailable()>0)
    			{
    				if(!availability.getRoomName().equals(failName))
    				{
    				if(!(availability.getRoomName().equals(tempName)))
    				{
    					
    					 if (!tempName.equals("")) {
    				            
    			             
    			              
    						 System.out.println("mpika sto prwto");
    						 System.out.println("timh tou tempdate einai"+tempDate);
    			                if (tempDate.isBefore(toDate.minusDays(1))) {
    			                    System.out.println("se epiasa");
    			                	availableTypes.remove(availableTypes.size()-1);

    			                }

    			              }
    				System.out.println("there are rooms this day");
    			
    				System.out.println("type of rooms is"+availability.getType());
    				System.out.println("tempType is"+availability.getType());
    				if(availability.getDate().equals(fromDate))
    					availableTypes.add(availability);
    				System.out.println("after avail types");
    				tempName=new String(availability.getRoomName());
    				tempDate=availability.getDate();
    				System.out.println("timh tou tempdate ananeothike"+tempDate);
    				}
    				else
    				{
    					System.out.println("timh tou tempdate ananeothike"+tempDate);
        				tempDate=availability.getDate();
    					System.out.println("after mpika sto else");
    					if(availableTypes.size()>0){
    					if(availability.getAvailable()<availableTypes.get(availableTypes.size()-1).getAvailable())
    					{
    						availableTypes.remove(availableTypes.size()-1);
    						availableTypes.add(availability);
    					}
    					}
    				}
    				}
    			}
    			else
    			{
    				
    				if(availability.getType().equals(tempName))
    				{
    				System.out.println("no rooms this day");
    				if(availableTypes.size()<0)
    					availableTypes.remove(availableTypes.size()-1);
    				}
    				else
    					System.out.println("mpika sto fail type");
    					failName=new String(availability.getRoomName());
    			}
    			
    			
    			
    		}
    		
    	}
    	 if (tempDate.isBefore(toDate.minusDays(1))) {
             System.out.println("se epiasa");
            if(availableTypes.size()>0)
         	availableTypes.remove(availableTypes.size()-1);

         }
    	for (Availability avail : availableTypes)
		{
    		
			int numberOfPersonsPerRoom;
			if(avail.getType().equals("Si"))
			{
				numberOfPersonsPerRoom=1;
			}
			else if(avail.getType().equals("Db"))
			{
				numberOfPersonsPerRoom=2;
			}
			
			else
				numberOfPersonsPerRoom=3;
		    System.out.println("i am in availability with date"+avail.getDate()+"and my id is ->"+avail.getId());

				
			sum+=numberOfPersonsPerRoom*avail.getAvailable();
		}
    	System.out.println("availableTypes size is"+availableTypes.size());
    	System.out.println("sum is"+sum);
    	
		if(sum>=numberOfPersons)
		{
			System.out.println("i add hotel and my id is"+hotel.getId());
			hotelsAfterAvailabilityCheck.add(hotel);
			
			
		}
    	
    }
    System.out.println("size of hotel after number check is -> "+hotelsAfterAvailabilityCheck.size());
    final int start = (int)pageable.getOffset();
    System.out.println("value of the offset is ->  "+start);
    final int end = Math.min((start + pageable.getPageSize()), hotelsAfterAvailabilityCheck.size());
    System.out.println("value of the end is ->  "+end);
    final Page<Hotel> hotelsPage = new PageImpl<>(hotelsAfterAvailabilityCheck.subList(start, end), pageable, hotelsAfterAvailabilityCheck.size());
    
   
    

    return hotelsPage;
}
@GetMapping("/testVariable/{stringFromDate}")
public void test(@PathVariable String stringFromDate)
{ 
	LocalDate fromDate=LocalDate.parse(stringFromDate);
	

}
@DeleteMapping("/deleteHotels")
public ResponseEntity<?> deleteHotels() {
	
    this.hotelRepository.deleteAll();
	return ResponseEntity.ok(new MessageResponse("Hotels deleted successfully!"));

}
}
