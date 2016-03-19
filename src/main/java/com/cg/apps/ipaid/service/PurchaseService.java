package com.cg.apps.ipaid.service;

import java.util.List;

import com.cg.apps.ipaid.entity.Purchase;
import com.cg.apps.ipaid.request.PurchaseRequest;

public interface PurchaseService {

	public void savePurchase(PurchaseRequest purchaseRequest);

	List<Purchase> fetchPurchaseDetails(String key, String value);

	List<String> fetchDistinctProductNames();
}
