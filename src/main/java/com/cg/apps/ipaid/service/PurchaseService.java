package com.cg.apps.ipaid.service;

import java.util.List;

import com.cg.apps.ipaid.response.PurchaseRequest;
import com.cg.apps.ipaid.response.PurchaseResponse;

public interface PurchaseService {

	public List<PurchaseResponse> fetchPurchaseDetails(String key, String value);
	
	public void savePurchase(PurchaseRequest purchaseRequest);

	List<String> fetchDistinctProductNames();
}
