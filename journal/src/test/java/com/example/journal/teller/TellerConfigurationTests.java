/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.journal.teller;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.journal.JournalEntry;
import com.example.journal.teller.TellerConfigurationTests.TestConfiguration;

/**
 * @author Dave Syer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestConfiguration.class)
public class TellerConfigurationTests {
	
	@Autowired
	private StreamJournal journal;

	@Autowired
	private JournalSource source;

	@Autowired
	private MessageCollector collector;

	@Test
	public void test() throws Exception {
		journal.credit(new JournalEntry("account", "test", "id", Money.parse("GBP1000")));
		Message<?> message = collector.forChannel(source.output()).poll(1000, TimeUnit.MILLISECONDS);
		assertThat(message.getHeaders(), hasKey("X-Journal-Type"));
	}
	
	@Configuration
	@EnableAutoConfiguration
	@Import({TellerConfiguration.class, TestSupportBinderAutoConfiguration.class})
	protected static class TestConfiguration {
		
	}

}
