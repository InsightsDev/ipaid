package com.cg.apps.ipaid.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cg.apps.ipaid.logging.Loggable;
import com.cg.apps.ipaid.response.UserResponse;
import com.cg.apps.ipaid.service.AppUserDetailsService;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	AppUserDetailsService appUserDetailsService;
	
	@Loggable
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody UserResponse fetchUserDetails(Principal principal) {
		final String loggedInUser = principal.getName();
		logger.info("User logged in is: {}", loggedInUser);
		return appUserDetailsService.getUserDetails(loggedInUser);
	}
	
	@Loggable
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public @ResponseBody UserResponse editUserDetails(@RequestBody(required = true) UserResponse userRequest, @RequestParam(name = "file") MultipartFile file, Principal principal) throws Exception {
		return appUserDetailsService.editAndSaveUserDetails(userRequest, file, principal.getName());
	}
	
//	@RequestMapping(value = "/userProfile", method = RequestMethod.GET)
//	public String userProfile() {
//		return "userProfile";
//	}
}
