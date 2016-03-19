package com.cg.apps.ipaid.util;

import javax.mail.Message;

import com.cg.apps.ipaid.entity.PurchaseMetaData;
import com.cg.apps.ipaid.request.PurchaseRequest;

public interface EmailParser {
	PurchaseRequest parseEmailInvoice(Message message);
}
