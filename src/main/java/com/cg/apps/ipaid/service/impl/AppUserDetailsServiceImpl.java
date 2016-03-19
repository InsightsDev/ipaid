package com.cg.apps.ipaid.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cg.apps.ipaid.entity.User;
import com.cg.apps.ipaid.service.AppUserDetailsService;
import com.mongodb.gridfs.GridFSDBFile;

@Service
public class AppUserDetailsServiceImpl implements AppUserDetailsService {

	private static Logger LOGGER = LoggerFactory.getLogger(AppUserDetailsServiceImpl.class);

	@Autowired
    private GridFsOperations gridOperations;
	
	@Autowired
	private Mapper mapper;
	
	@Override
	public UserDetails loadUserByUsername(final String emailId) throws UsernameNotFoundException {
		User user = getUserByEmailId(emailId);
		if (null == user) {
			LOGGER.error("No data found for the user: {}", emailId);
			throw new UsernameNotFoundException("Unable to login");
		}
		
		
		LOGGER.info("Login user: {}", user);
		
		Collection<? extends GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		
		return new org.springframework.security.core.userdetails.User(user.getMetadata().getEmailId(), user.getMetadata().getPassword(), grantedAuthorities);
	}

	@Override
	public User getUserByEmailId(final String emailId) {
		GridFSDBFile result = gridOperations.findOne(new Query().addCriteria(Criteria.where("metadata.emailId").is(emailId)));
		return mapper.map(result, User.class);
	}
	
}
