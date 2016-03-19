package com.cg.apps.ipaid.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.cg.apps.ipaid.IPaidApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IPaidApplication.class)
@WebAppConfiguration
public class EmailReaderTest {

	@Autowired
	private EmailReader emailReader;

	@Test
	public void testScanUnreadEmails() {
		//fail("Not yet implemented");
		emailReader.scanUnreadEmails();
	}

}
