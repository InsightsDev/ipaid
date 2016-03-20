package com.cg.apps.ipaid.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;

import com.cg.apps.ipaid.entity.Purchase;
import com.cg.apps.ipaid.entity.StoreSales;
import com.cg.apps.ipaid.entity.TrendResponse;
import com.cg.apps.ipaid.logging.Loggable;
import com.cg.apps.ipaid.response.PurchaseRequest;
import com.cg.apps.ipaid.response.PurchaseResponse;
import com.cg.apps.ipaid.service.PurchaseService;
import com.google.common.collect.Ordering;
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
	public List<PurchaseResponse> fetchPurchaseDetails(String key, String value) {
		List<GridFSDBFile> results = gridOperations.find(new Query().addCriteria(Criteria.where(key).is(value)));
		List<PurchaseResponse> purchases = new ArrayList<>();
		for(GridFSDBFile file: results) {
			Purchase purchase = mapper.map(file, Purchase.class);
			purchases.add(mapper.map(purchase, PurchaseResponse.class));
		}
		return purchases;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> fetchDistinctProductNames() {
		return mongoTemplate.getCollection("fs.files").distinct("productName");
	}

	public <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}

	public List<String> computeBestSellingProducts() {
		Map<String,Integer> productsMap = new HashMap<>();
		List<GridFSDBFile> results = gridOperations.find(new Query());
		for(GridFSDBFile file: results) {
			Purchase purchase = mapper.map(file, Purchase.class);
			String productName = purchase.getMetadata().getProductName();
			if(null != productsMap.get(productName)) {
				productsMap.put(productName, new Integer(productsMap.get(productName).intValue() + 1));
			} else {
				productsMap.put(productName,new Integer(1));
			}
		}
		List<Integer> top5 = Ordering.natural().greatestOf(productsMap.values(), 5);
		List<String> topProducts = new ArrayList<>();
		for(Integer i : top5) {
			topProducts.add(getKeyByValue(productsMap,i));
		}
		return topProducts;
	}
	public List<TrendResponse> fetchHotTrends() {
		Map<String,Map<String,Integer>> productsMap = new HashMap<>();
		List<GridFSDBFile> results = gridOperations.find(new Query());
		for(GridFSDBFile file: results) {
			Purchase purchase = mapper.map(file, Purchase.class);
			String storeName = purchase.getMetadata().getStoreName();
			String productName = purchase.getMetadata().getProductName();

			if(null != productsMap.get(productName)) {
				Map<String,Integer> storesMap = productsMap.get(productName);
				if(null != storesMap.get(storeName)) {
					storesMap.put(storeName, new Integer(storesMap.get(storeName).intValue() + 1));
				} else {
					storesMap.put(storeName, new Integer(1));
				}
			} else if(null != productName){
				Map<String,Integer> storesMap = new HashMap<>();
				storesMap.put(storeName, new Integer(1));
				productsMap.put(productName, storesMap);
			}

		}
		List<String> topProducts = computeBestSellingProducts();
		List<TrendResponse> trendResponse = new ArrayList<>();
		for(String product: topProducts) {
			TrendResponse trend = new TrendResponse();
			trend.setProductName(product);
			List<StoreSales> storeSales = new ArrayList<>();
			Map<String,Integer> stores = productsMap.get(product);
			if(null != stores && null != stores.entrySet()) {
				for(Entry<String,Integer> entry:stores.entrySet()) {
					StoreSales sale = new StoreSales();
					sale.setStoreName(entry.getKey());
					sale.setSales(entry.getValue());
					storeSales.add(sale);
				}
			}		
			trend.setStoreSales(storeSales);
			trendResponse.add(trend);
		}
		return trendResponse;
	}
}
