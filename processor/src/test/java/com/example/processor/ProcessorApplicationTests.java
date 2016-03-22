package com.example.processor;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.example.processor.ProcessorApplicationTests.TestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class ProcessorApplicationTests {

	@Autowired
	private Processor processor;

	@Autowired
	private MessageCollector collector;

	@Test
	public void contextLoads() throws Exception {
		processor.input().send(MessageBuilder
				.withPayload(new Payment("foo", "bar", Money.parse("GBP1000"))).build());
		assertThat(collector.forChannel(processor.output()).poll(1000,
				TimeUnit.MILLISECONDS), is(notNullValue()));
	}

	@Configuration
	@Import({ TestSupportBinderAutoConfiguration.class, ProcessorApplication.class })
	protected static class TestApplication {}

}
