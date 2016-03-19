package com.cg.apps.ipaid.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.cg.apps.ipaid.entity.User;

@Service
public interface AppUserDetailsService extends UserDetailsService {
	
	User getUserByEmailId(final String emailId);

}
