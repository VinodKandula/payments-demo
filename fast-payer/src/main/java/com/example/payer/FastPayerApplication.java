package com.example.payer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBinding(Sink.class)
public class FastPayerApplication {

	private static Logger log = LoggerFactory.getLogger(FastPayerApplication.class);

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
		log.info("Paid: " + payment);
	}

	public static void main(String[] args) {
		SpringApplication.run(FastPayerApplication.class, args);
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
