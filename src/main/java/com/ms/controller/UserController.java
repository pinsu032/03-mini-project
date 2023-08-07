package com.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.dto.User;
import com.ms.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService service;
	
	@PostMapping("/")
	public ResponseEntity<String> saveUser(@RequestBody User user){
		boolean status = service.registerEmployee(user);
		
		if(status)
		  return new ResponseEntity<String>("mail sent",HttpStatus.OK);
		else
		  return new ResponseEntity<String>("mail not sent",HttpStatus.OK);

	}
}
