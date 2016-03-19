package com.cg.apps.ipaid.util;

public enum InvoiceTypes {
	FLIPKART;

	public static EmailParser getEmailParserInstance(InvoiceTypes invoiceType) throws Exception {
		EmailParser emailParser = null;
		switch (invoiceType) {
		case FLIPKART:
			emailParser = new FlipkartEmailParser();
			break;
		default:
			throw new Exception("Vendor not supported yet!");
		}
		return emailParser;
	}
}