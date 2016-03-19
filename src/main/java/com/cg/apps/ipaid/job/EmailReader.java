package com.cg.apps.ipaid.job;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmailReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailReader.class);

	private enum INVOICE_TYPES {
		FLIPKART, AMAZON;
	}

	@Value("${ipaid.mail}")
	private String email;

	@Value("${ipaid.mail.smtp.host}")
	private String host;

	@Value("${ipaid.mail.smtp.socketFactory.class}")
	private String socketFactoryClass;

	@Value("${ipaid.mail.smtp.socketFactory.port}")
	private int socketFactoryPort;

	@Value("${ipaid.mail.smtp.port}")
	private int port;

	@Value("${ipaid.mail.smtp.auth}")
	private boolean isAuthRequired;

	@Value("${ipaid.mail.user}")
	private String userName;

	@Value("${ipaid.mail.pwd}")
	private String password;

	@Scheduled(fixedDelay = 60000)
	public void scanUnreadEmails() {
		Properties properties = System.getProperties();
		try {
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.user", userName);
			properties.put("mail.smtp.starttls.enable", isAuthRequired);
			properties.put("mail.smtp.socketFactory.port", socketFactoryPort);
			properties.put("mail.smtp.socketFactory.class", socketFactoryClass);
			properties.put("mail.smtp.auth", isAuthRequired);

			Session session = Session.getDefaultInstance(properties);

			Store store = session.getStore("imaps");
			store.connect(host, userName, password);
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_WRITE);

			// search for all "unseen" messages
			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			Message messages[] = inbox.search(unseenFlagTerm);

			if (messages.length == 0)
				LOGGER.warn("No messages found.");

			for (int i = 0; i < messages.length; i++) {
				LOGGER.info("Message " + (i + 1));
				LOGGER.info("From : " + messages[i].getFrom()[0]);
				LOGGER.info("Subject : " + messages[i].getSubject());
				LOGGER.info("Sent Date : " + messages[i].getSentDate());
				parseEmailContent(messages[i]);
				messages[i].setFlag(Flags.Flag.SEEN, true);
			}

			inbox.close(true);
			store.close();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseEmailContent(final Message message) {
		try {
			if (null != message) {
				final String subject = message.getSubject();
				if (!StringUtils.isEmpty(subject)) {
					for (INVOICE_TYPES invoiceType: INVOICE_TYPES.values()) {
						if(subject.toLowerCase().contains(invoiceType.name().toLowerCase())) {
							LOGGER.info("Invoice Type: {}", invoiceType.name());
						}
					}
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
}
