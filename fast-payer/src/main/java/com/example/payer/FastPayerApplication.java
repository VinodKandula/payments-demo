package com.example.payer;

import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.example.journal.Journal;
import com.example.journal.JournalEntry;
import com.example.journal.teller.TellerConfiguration;

@SpringBootApplication
@EnableBinding(Sink.class)
@Import(TellerConfiguration.class)
public class FastPayerApplication {

	private static Logger log = LoggerFactory.getLogger(FastPayerApplication.class);

	@Autowired
	private Journal journal;

	@Value("${ingester.account:fp}")
	private String account;

	@Bean
	public InitializingBean initializer(Sink sink) {
		return () -> {
			sink.input().subscribe(message -> {
				FastPayment payment = (FastPayment) message.getPayload();
				pay(payment);
			});
		};
	}

	public void pay(FastPayment payment) {
		journal.debit(new JournalEntry(account, "fast-payer", payment.getId(),
				payment.getAmount()));
		log.info("Paid: " + payment);
	}

	public static void main(String[] args) {
		SpringApplication.run(FastPayerApplication.class, args);
	}
}

class FastPayment {

	private String msg;
	private Money amount;
	private String id;

	@SuppressWarnings("unused")
	private FastPayment() {
	}

	public FastPayment(String msg, String id, Money amount) {
		this.msg = msg;
		this.id = id;
		this.amount = amount;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Money getAmount() {
		return amount;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "FastPayment [id=" + id + ", amount=" + amount + ", msg="
				+ msg.substring(0, 40).replaceAll("\n", " ") + "]";
	}

}