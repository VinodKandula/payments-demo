package com.example.processor;

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
	
	@Transformer(inputChannel=Processor.INPUT, outputChannel="payments")
	public Payment convert(String inbound) {
		return new Payment(inbound);
	}

	@Router(inputChannel="payments")
	public String route(Payment inbound) {
		return "fp";
	}

	@ServiceActivator(inputChannel="fp", outputChannel=Processor.OUTPUT)
	public FastPayment pay(Payment inbound) {
		log.info("Fast payment: " + inbound);
		return new FastPayment(inbound.getMsg());
	}

	public static void main(String[] args) {
		SpringApplication.run(ProcessorApplication.class, args);
	}
}

class Payment {

	private String msg;

	@SuppressWarnings("unused")
	private Payment() {
	}

	public Payment(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "Payment [msg=" + msg.substring(0, 40).replaceAll("\n", " ") + "]";
	}

}

class FastPayment {

	private String msg;

	@SuppressWarnings("unused")
	private FastPayment() {
	}

	public FastPayment(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "FastPayment [msg=" + msg.substring(0, 40).replaceAll("\n", " ") + "]";
	}

}