package com.cg.apps.ipaid.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;

import com.cg.apps.ipaid.entity.Purchase;
import com.cg.apps.ipaid.logging.Loggable;
import com.cg.apps.ipaid.request.PurchaseRequest;
import com.cg.apps.ipaid.service.PurchaseService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@Service
public class PurchaseServiceImpl implements PurchaseService {

	@Autowired
    private GridFsOperations gridOperations;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	@Override
	@Loggable
	public void savePurchase(PurchaseRequest purchaseRequest) {
		DBObject metaData = new BasicDBObject();
        metaData.put("productName", purchaseRequest.getProductName());
        metaData.put("invoiceNo", purchaseRequest.getInvoiceNo());
        metaData.put("productCost", purchaseRequest.getProductCost());
        metaData.put("productCode", purchaseRequest.getProductCode());
        metaData.put("location", purchaseRequest.getLocation());
        metaData.put("storeName", purchaseRequest.getStoreName());
        metaData.put("userId", purchaseRequest.getUserId());
        metaData.put("purchaseDate", purchaseRequest.getPurchaseDate());

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(purchaseRequest.getBill());
            gridOperations.store(inputStream, purchaseRequest.getBill().getName(), "application/octet-stream", metaData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}

	@Override
	@Loggable
	public List<Purchase> fetchPurchaseDetails(String key, String value) {
		List<GridFSDBFile> results = gridOperations.find(new Query().addCriteria(Criteria.where(key).is(value)));
		List<Purchase> purchases = new ArrayList<>();
		for(GridFSDBFile file: results) {
			Purchase purchase = mapper.map(file, Purchase.class);
			purchases.add(purchase);
		}
		return purchases;
	}

	@Override
	public List<String> fetchDistinctProductNames() {
		return mongoTemplate.getCollection("fs.files").distinct("productName");
	}
}
