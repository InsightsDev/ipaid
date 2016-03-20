package com.cg.apps.ipaid.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cg.apps.ipaid.response.UserResponse;

@Service
public interface AppUserDetailsService extends UserDetailsService {
	
	UserResponse getUserDetails(final String emailId);

	UserResponse editAndSaveUserDetails(UserResponse userResponse, MultipartFile file, String emailId) throws Exception;

}
