package com.cg.apps.ipaid.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
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
import org.springframework.web.multipart.MultipartFile;

import com.cg.apps.ipaid.entity.User;
import com.cg.apps.ipaid.entity.UserMetadata;
import com.cg.apps.ipaid.logging.Loggable;
import com.cg.apps.ipaid.response.PurchaseResponse;
import com.cg.apps.ipaid.response.UserResponse;
import com.cg.apps.ipaid.service.AppUserDetailsService;
import com.cg.apps.ipaid.service.PurchaseService;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Service
public class AppUserDetailsServiceImpl implements AppUserDetailsService {

	private static Logger LOGGER = LoggerFactory.getLogger(AppUserDetailsServiceImpl.class);

	@Autowired
    private GridFsOperations gridOperations;
	
	@Autowired
	private Mapper mapper;
	
	@Autowired
	private PurchaseService purchaseService;
	
	@Loggable
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

	@Loggable
	private User getUserByEmailId(final String emailId) {
		GridFSDBFile result = gridOperations.findOne(new Query().addCriteria(Criteria.where("metadata.emailId").is(emailId)));
		return mapper.map(result, User.class);
	}

	@Loggable
	@Override
	public UserResponse getUserDetails(String emailId) {
		User user = getUserByEmailId(emailId);
		String image = null;
		UserMetadata userMetadata = user.getMetadata();
		UserResponse userResponse = new UserResponse();
		userResponse.setFirstName(userMetadata.getFirstName());
		userResponse.setLastName(userMetadata.getLastName());
		userResponse.setEmailId(userMetadata.getEmailId());
		userResponse.setPhoneNumber(userMetadata.getPhoneNumber());
		try {
			image = getImage(user.getId());
		} catch (Exception e) {
			LOGGER.trace("Failed to load image with Id: {} with exception...", user.getId(), e);
		}
		if(null == image) {
			image = "data:image/png;base64,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";
		}
		userResponse.setImage(image);
		List<PurchaseResponse> purchases = purchaseService.fetchPurchaseDetails("metadata.userId", emailId);
		if(CollectionUtils.isNotEmpty(purchases)) {
			userResponse.setPurchases(purchases);
		}
		return userResponse;
	}
	
	@Loggable
	@Override
	public UserResponse editAndSaveUserDetails(final UserResponse userResponse, final MultipartFile file, final String emailId) throws Exception {
		UserMetadata saveUserMetadata = mapper.map(userResponse, UserMetadata.class);
	    InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(getFileFromMultipartRequest(file));
            GridFSFile savedUser = gridOperations.store(inputStream, emailId, "application/octet-stream", saveUserMetadata);
            return mapper.map(savedUser, UserResponse.class);
        } catch (Exception e) {
            LOGGER.error("Failed to save data of user: {}, with the exception...", emailId, e);
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	@Loggable
	private File getFileFromMultipartRequest(MultipartFile file) throws Exception {
		File convFile = null;
		try{
			convFile = new File("D:/" +file.getOriginalFilename());
		    convFile.createNewFile(); 
		    FileOutputStream fos = new FileOutputStream(convFile); 
		    fos.write(file.getBytes());
		    fos.close(); 
		}catch(Exception e) {
			LOGGER.warn("Failed to read from file");
			throw new Exception("Failed to save the image uploaded");
		}
		return convFile;
	}
	
	@Loggable
	private String getImage(String id) throws Exception {
		ObjectId objectId = new ObjectId(id);
		InputStream imageSteam = gridOperations.findOne(Query.query(Criteria.where("_id").is(objectId))).getInputStream();
		return Base64.encodeBase64(IOUtils.toByteArray(imageSteam)).toString();
	}
}
