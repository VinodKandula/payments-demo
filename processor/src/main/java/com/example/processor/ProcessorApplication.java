package com.example.processor;

import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;

@SpringBootApplication
@EnableBinding(Processor.class)
@MessageEndpoint
public class ProcessorApplication {

	private static Logger log = LoggerFactory.getLogger(ProcessorApplication.class);

	@Transformer(inputChannel = Processor.INPUT, outputChannel = "payments")
	public Payment enhance(Payment inbound) {
		// TODO: do stuff to the payment
		return inbound;
	}

	@Router(inputChannel = "payments")
	public String route(Payment inbound) {
		return "fp";
	}

	@ServiceActivator(inputChannel = "fp", outputChannel = Processor.OUTPUT)
	public FastPayment pay(Payment inbound) {
		log.info("Fast payment: " + inbound);
		return new FastPayment(inbound.getMsg(), inbound.getId(), inbound.getAmount());
	}

	public static void main(String[] args) {
		SpringApplication.run(ProcessorApplication.class, args);
	}
}

class Payment {

	private String msg;
	private Money amount;
	private String id;

	@SuppressWarnings("unused")
	private Payment() {
	}

	public Payment(String msg, String id, Money amount) {
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
		return "Payment [id=" + id + ", amount=" + amount + ", msg="
				+ msg.substring(0, 40).replaceAll("\n", " ") + "]";
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

	@Override
	public String toString() {
		return "FastPayment [id=" + id + ", amount=" + amount + ", msg="
				+ msg.substring(0, 40).replaceAll("\n", " ") + "]";
	}

	public Money getAmount() {
		return amount;
	}

	public String getId() {
		return id;
	}

}