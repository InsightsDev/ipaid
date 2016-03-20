package com.cg.apps.ipaid.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cg.apps.ipaid.entity.TrendResponse;
import com.cg.apps.ipaid.logging.Loggable;
import com.cg.apps.ipaid.ocr.ImageExtractor;
import com.cg.apps.ipaid.response.PurchaseRequest;
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
    public List<PurchaseResponse> fetchPurchaseDetailsForUserId(@RequestParam String user){ 
		List<PurchaseResponse> purchase = purchaseService.fetchPurchaseDetails("metadata.userId", user);
		return purchase;
	}

	@RequestMapping(value="/searchProduct", method = RequestMethod.GET)
    public List<PurchaseResponse> searchProduct(@RequestParam String productName){
		List<PurchaseResponse> purchases = purchaseService.fetchPurchaseDetails("metadata.productName", productName);
		Collections.sort(purchases);
		return purchases;
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
	
	@RequestMapping(value="/hotTrends")
	public List<TrendResponse> hotTrends() {
		return purchaseService.fetchHotTrends();
	}
	
	
}
