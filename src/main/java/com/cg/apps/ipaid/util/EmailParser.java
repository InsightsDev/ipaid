package com.cg.apps.ipaid.util;

import javax.mail.Message;

public interface EmailParser {
	boolean parseEmailInvoice(Message message);
}
