package com.cg.apps.ipaid.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import com.cg.apps.ipaid.request.PurchaseRequest;
import com.cg.apps.ipaid.service.PurchaseService;

public class PurchaseServiceImpl implements PurchaseService {

	@Autowired
    private GridFsOperations gridOperations;
	
	@Override
	public void savePurchase(PurchaseRequest purchaseRequest) {
				
	}

	
}
