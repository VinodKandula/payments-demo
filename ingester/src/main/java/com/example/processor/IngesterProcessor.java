package com.example.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Splitter;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import com.example.journal.Journal;
import com.example.journal.JournalEntry;
import com.example.journal.teller.TellerConfiguration;

@SpringBootApplication
@EnableBinding(Processor.class)
@MessageEndpoint
@Import(TellerConfiguration.class)
public class IngesterProcessor {

	private static Logger log = LoggerFactory.getLogger(IngesterProcessor.class);

	@Autowired
	private Journal journal;

	@Value("${ingester.account:ca}")
	private String account;

	@Splitter(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public List<Mt103Payment> split(@Payload byte[] file,
			@Header("file_name") String name) throws Exception {
		List<Mt103Payment> payments = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(file)))) {
			String line = reader.readLine();
			StringBuilder message = new StringBuilder();
			while (line != null) {
				message.append(line).append("\n");
				if (line.equals("-}")) {
					Mt103Payment payment = new Mt103Payment(message.toString());
					log.info("Found message: {}", payment);
					payments.add(payment);
					sendToJournal(payment, name);
					message = new StringBuilder();
				}
				line = reader.readLine();
			}
		}
		return payments;
	}

	private void sendToJournal(Mt103Payment payment, String source) {
		journal.credit(
				new JournalEntry(account, source, payment.getId(), payment.getAmount()));
	}

}

class Mt103Payment {

	private String msg;
	private Money amount;
	private String id;

	@SuppressWarnings("unused")
	private Mt103Payment() {
	}

	public String getId() {
		return id;
	}

	public Money getAmount() {
		return amount;
	}

	public Mt103Payment(String msg) {
		setMsg(msg);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
		amount = Money.of(CurrencyUnit.GBP, randomAmount());
		try {
			id = Base64.getEncoder().encodeToString(
					MessageDigest.getInstance("md5").digest(msg.getBytes("UTF-8")));
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot encode String", e);
		}
	}

	private BigDecimal randomAmount() {
		Random source = new Random();
		double random = source.nextGaussian();
		while (random < -2) {
			random = source.nextGaussian();
		}
		int mean = 1000;
		return new BigDecimal(
				BigInteger.valueOf(mean * 100 + (long) (random * (mean * 100 / 2))), 2);
	}

	@Override
	public String toString() {
		return "Mt103Payment [msg=" + msg.substring(0, 40).replaceAll("\n", " ")
				+ ", amount=" + getAmount() + "]";
	}

}
