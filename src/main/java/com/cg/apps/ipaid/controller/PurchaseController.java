package com.cg.apps.ipaid.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cg.apps.ipaid.logging.Loggable;
import com.cg.apps.ipaid.request.PurchaseRequest;
import com.cg.apps.ipaid.service.PurchaseService;

@RestController
@RequestMapping(value = "/purchase")
public class PurchaseController {

	@Autowired
	private PurchaseService purchaseService;
	
	@Loggable
	@RequestMapping(value="/save", method = RequestMethod.GET)
    public void savePurchaseDetails(){ 
		PurchaseRequest request = new PurchaseRequest();
		request.setInvoiceNo("123");
		request.setLocation("bangalore");
		request.setProductCode("AB123");
		request.setProductCost(100000.0);
		request.setProductName("Macbook");
		request.setStoreName("Reliance");
		request.setUserId("arun_mohan@gmail.com");
		request.setBill(new File("testImages/1.jpg"));
		purchaseService.savePurchase(request);
	}
}
