package com.stacksimplify.restservices.controllers;

import java.util.List;
import java.util.Optional;

import javax.sound.sampled.Line;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stacksimplify.restservices.entities.Order;
import com.stacksimplify.restservices.entities.User;
import com.stacksimplify.restservices.exceptions.UserNotFoundException;
import com.stacksimplify.restservices.repository.UserRepository;
import com.stacksimplify.restservices.services.UserService;

@RestController
@RequestMapping(value="/hateoas/users")
@Validated
public class UserHateoasController {
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private UserService userService;
	
	
	
    @GetMapping("/{id}")
    public Resource<User> getUserById(@PathVariable("id") @Min(1) Long id){
        try {
        	Optional<User> userOptional = userService.getUserById(id);
        	User user = userOptional.get();
        	Long userid = user.getUserid();
        	Link selfLink = ControllerLinkBuilder.linkTo(this.getClass()).slash(userid).withSelfRel();
        	user.add(selfLink);
        	Resource<User> finalResource = new Resource<User>(user);
        
            return finalResource;
            
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    
    

    @GetMapping
    public Resources<User>  getAllUsers() throws UserNotFoundException{
    List<User> allusers = userService.getAllUsers();
    for (User user: allusers) {
    	//Self link
    	Long userid = user.getUserid();
    	Link selflink= ControllerLinkBuilder.linkTo(this.getClass()).slash(userid).withSelfRel();
    	user.add(selflink);
    	
    	//Relationship link with getAllOrders
    	Resources<Order> orders = ControllerLinkBuilder.methodOn(OrderHateoasController.class).getAllOrders(userid);
    	Link orderslink = ControllerLinkBuilder.linkTo(orders).withRel("all-orders");
    	user.add(orderslink);
 	
	}
    //self link for getAllUsers
    Link selfLinkGetAllUsers = ControllerLinkBuilder.linkTo(this.getClass()).withSelfRel();
    Resources<User> finalResources = new Resources<User>(allusers,selfLinkGetAllUsers);
    
        return  finalResources;
    }
	

}
