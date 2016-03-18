package com.example.ingester;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Splitter;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@SpringBootApplication
@EnableBinding(Processor.class)
@MessageEndpoint
public class IngesterProcessor {

	private static Logger log = LoggerFactory.getLogger(IngesterProcessor.class);

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
					message = new StringBuilder();
				}
				line = reader.readLine();
			}
		}
		return payments;
	}

	public static void main(String[] args) {
		SpringApplication.run(IngesterProcessor.class, args);
	}
}

class Mt103Payment {

	private String msg;

	@SuppressWarnings("unused")
	private Mt103Payment() {
	}

	public Mt103Payment(String msg) {
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
		return "Mt103Payment [msg=" + msg.substring(0, 40).replaceAll("\n", " ") + "]";
	}

}
