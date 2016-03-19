package com.cg.apps.ipaid.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cg.apps.ipaid.entity.Purchase;
import com.cg.apps.ipaid.logging.Loggable;
import com.cg.apps.ipaid.ocr.ImageExtractor;
import com.cg.apps.ipaid.request.PurchaseRequest;
import com.cg.apps.ipaid.response.PurchaseResponse;
import com.cg.apps.ipaid.service.PurchaseService;

@RestController
@RequestMapping(value = "/purchase")
public class PurchaseController {

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	private Mapper mapper;
	
	@Loggable
	@RequestMapping(value="/fetchUserPurchases", method = RequestMethod.GET)
    public List<Purchase> fetchPurchaseDetailsForUserId(@RequestParam String user){
		List<Purchase> purchase = purchaseService.fetchPurchaseDetails("metadata.userId", user);
		return purchase;
	}

	@Loggable
	@RequestMapping(value="/searchProduct", method = RequestMethod.GET)
    public List<PurchaseResponse> searchProduct(@RequestParam String productName){
		List<PurchaseResponse> responseList = new ArrayList<>();
		List<Purchase> purchases = purchaseService.fetchPurchaseDetails("metadata.productName", productName);
		Collections.sort(purchases);
		for(int i=0;i<3 && i<purchases.size(); i++) {
			PurchaseResponse response = mapper.map(purchases.get(i).getMetadata(),PurchaseResponse.class);
			responseList.add(response);
		}
		return responseList;
	}

	@RequestMapping(value="/upload", method=RequestMethod.POST)
    public void handleFileUpload(@RequestParam("user") String user, @RequestParam("file") MultipartFile file){
		File convFile = null;
		try{
			convFile = new File(file.getOriginalFilename());
		    convFile.createNewFile();
		    FileOutputStream fos = new FileOutputStream(convFile);
		    fos.write(file.getBytes());
		    fos.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		ImageExtractor processor = new ImageExtractor();
		PurchaseRequest request = processor.processExtractedText(processor.extractTextFromImage(convFile));
		request.setBill(convFile);
		request.setUserId(user);
		purchaseService.savePurchase(request);

    }

	@RequestMapping(value="/productName")
	public List<String> fetchDistinctProductNames() {
		return purchaseService.fetchDistinctProductNames();
	}
}
