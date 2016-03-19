package com.cg.apps.ipaid.ocr;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cg.apps.ipaid.request.PurchaseRequest;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ImageExtractor {

	public String extractTextFromImage(File imageFile) {
		String result = null;
		ITesseract instance = new Tesseract(); 
        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
	}
	
	public PurchaseRequest processExtractedText(String extractedText) {
		PurchaseRequest purchaseRequest = new PurchaseRequest();
		String[] lines = extractedText.split("\\r?\\n");
		List<String> list = new ArrayList<String>(Arrays.asList(lines));
		list.removeAll(Arrays.asList("",null));
		purchaseRequest.setStoreName(list.get(0));
		purchaseRequest.setLocation(list.get(2));
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).contains("Item Name")) {
				purchaseRequest.setProductName(list.get(i+1).replaceAll("[^A-Za-z]"," ").trim());
				break;
			}
		}
		for(String line : list) {
			if(line.contains("Date:")) {
				purchaseRequest.setPurchaseDate(line.substring(line.indexOf(":")+1,line.length()).trim());
			}else if (line.contains("Gross Amount")) {
				NumberFormat nf = NumberFormat.getInstance();
				String amt = line.substring(line.indexOf("Amount")+7,line.length());
				try {
					purchaseRequest.setProductCost(nf.parse(amt).doubleValue());
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(line.contains("Bill")) {
				purchaseRequest.setInvoiceNo(line.substring(line.indexOf(":")+1,line.indexOf("Time")-1).trim());
			}
		}
		return purchaseRequest;
	}
	
//	public static void main(String[] args){
//		File file = new File("testImages/bill1.png");
//		System.out.println(processExtractedText(extractTextFromImage(file)));
//	}
}
