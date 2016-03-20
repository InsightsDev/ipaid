package com.cg.apps.ipaid.entity;

import java.util.List;

public class TrendResponse {

	private String productName;
	private List<StoreSales> storeSales;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<StoreSales> getStoreSales() {
		return storeSales;
	}
	public void setStoreSales(List<StoreSales> storeSales) {
		this.storeSales = storeSales;
	}
	
	
}
