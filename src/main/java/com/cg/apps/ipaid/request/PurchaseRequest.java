package com.cg.apps.ipaid.request;

import java.io.File;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PurchaseRequest {

	private String productName;
	private String invoiceNo;
	private Double productCost;
	private String productCode;
	private String location;
	private String storeName;
	private String userId;
	private String purchaseDate;
	private File bill;

	public PurchaseRequest() {

	}

	public PurchaseRequest(String location, String storeName, String userId) {
		this.location = location;
		this.storeName = storeName;
		this.userId = userId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Double getProductCost() {
		return productCost;
	}

	public void setProductCost(Double productCost) {
		this.productCost = productCost;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public File getBill() {
		return bill;
	}

	public void setBill(File bill) {
		this.bill = bill;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
