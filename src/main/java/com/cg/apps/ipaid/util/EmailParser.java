package com.cg.apps.ipaid.util;

import javax.mail.Message;

import com.cg.apps.ipaid.response.PurchaseRequest;

public interface EmailParser {
	PurchaseRequest parseEmailInvoice(Message message);
}
