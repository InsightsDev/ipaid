package com.cg.apps.ipaid.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.cg.apps.ipaid.response.PurchaseRequest;

public class FlipkartEmailParser implements EmailParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlipkartEmailParser.class);
	private static final String INVOICE_FILE_PATH = "/Users/shishirkumar/git/ipaid/tempInvoices/";

	@Override
	public PurchaseRequest parseEmailInvoice(Message message) {
		PurchaseRequest purchaseRequest = null;
		String line = null;
		boolean nextProductLine = false;
		boolean nextProductCost = false;
		try {
			Address[] froms = message.getFrom();
			String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
			purchaseRequest = new PurchaseRequest("Online", "Flipkart", email);
			String filePath = saveInvoicePDF(message);
			if (StringUtils.isNoneEmpty(filePath)) {
				BodyContentHandler handler = new BodyContentHandler();
				Metadata metadata = new Metadata();
				FileInputStream inputstream = new FileInputStream(new File(filePath));
				ParseContext pcontext = new ParseContext();

				// parsing the document using PDF parser
				PDFParser pdfparser = new PDFParser();
				pdfparser.parse(inputstream, handler, metadata, pcontext);

				String invoiceData = handler.toString();

				// getting the content of the document
				LOGGER.info("Contents of the PDF: {}", invoiceData);

				BufferedReader bufReader = new BufferedReader(new StringReader(invoiceData));

				while ((line = bufReader.readLine()) != null) {
					if (StringUtils.isNoneBlank(line)) {
						if (nextProductLine) {
							purchaseRequest.setProductName(line.trim());
						} else if(nextProductCost) {
							Double cost = Double.valueOf(line.trim());
							purchaseRequest.setProductCost(cost);
						}
						// find product name
						if (StringUtils.contains(line, "WID")) {
							nextProductLine = true;
						} else {
							nextProductLine = false;
						}
						// find invoice number
						if(StringUtils.contains(line, "Invoice No") && StringUtils.contains(line, ":")) {
							purchaseRequest.setInvoiceNo(StringUtils.split(line, ":")[1].trim());
						}
						if(StringUtils.contains(line, "Order Date:")) {
							String purchaseDate = StringUtils.replace(line, "Order Date:", StringUtils.EMPTY).trim();
							LOGGER.info("purchaseDate: {}", purchaseDate);
							purchaseRequest.setPurchaseDate(purchaseDate);
						}
						// find price
						if(StringUtils.contains(line, "Grand Total")) {
							nextProductCost = true;
						} else {
							nextProductCost = false;
						}
						//prevLine = line;
					}
				}

				String[] metadataNames = metadata.names();

				for (String name : metadataNames) {
					LOGGER.debug("{}: {}", name, metadata.get(name));
				}
			}
			LOGGER.info("{}", purchaseRequest);
		} catch (IOException | SAXException | TikaException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return purchaseRequest;
	}

	private String saveInvoicePDF(Message message) {
		String filePath = null;
		try {
			if (message.getContent() instanceof Multipart) {
				Multipart multipart = (Multipart) message.getContent();
				for (int i = 0; i < multipart.getCount(); i++) {
					Part part = multipart.getBodyPart(i);
					String disposition = part.getDisposition();
					if ((disposition != null) && ((disposition.equalsIgnoreCase(Part.ATTACHMENT)
							|| (disposition.equalsIgnoreCase(Part.INLINE))))) {
						MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
						String fileName = mimeBodyPart.getFileName();
						filePath = INVOICE_FILE_PATH + fileName;
						File fileToSave = new File(filePath);
						mimeBodyPart.saveFile(fileToSave);
					}
				}
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
		return filePath;
	}

}
