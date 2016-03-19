package com.cg.apps.ipaid.service;

import java.util.List;

import com.cg.apps.ipaid.response.PurchaseResponse;

public interface PurchaseService {

	public void savePurchase(PurchaseResponse purchaseRequest);
		
	public List<PurchaseResponse> fetchPurchaseDetails(String key, String value);
}
