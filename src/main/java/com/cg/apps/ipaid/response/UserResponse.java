package com.cg.apps.ipaid.response;

import java.util.List;

public class UserResponse {

	private String id;
	private String firstName;
	private String lastName;
	private String emailId;
	private Long phoneNumber;
	// This will have the base64 image.
	private String image;
	private List<PurchaseResponse> purchases;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<PurchaseResponse> getPurchases() {
		return purchases;
	}
	public void setPurchases(List<PurchaseResponse> purchases) {
		this.purchases = purchases;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public Long getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
