package com.example.ingester;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.aggregate.AggregateApplicationBuilder;
import org.springframework.cloud.stream.aggregate.SharedChannelRegistry;
import org.springframework.cloud.stream.module.file.source.FileSourceApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@EnableAutoConfiguration
@Configuration
@Controller
public class IngesterApplication {

	@Autowired
	private SharedChannelRegistry registry;
	
	// TODO: use custom namespace
	private String namespace = IngesterProcessor.class.getName() + "_1";

	@RequestMapping(value="/bytes", method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> upload(@RequestBody byte[] file) {
		MessageChannel channel = registry.get(namespace + ".input");
		channel.send(MessageBuilder.withPayload(file).build());
		return Collections.singletonMap("status", "OK");
	}

	@RequestMapping(value="/file", method=RequestMethod.POST)
	public String file(@RequestParam MultipartFile file) throws Exception {
		MessageChannel channel = registry.get(namespace + ".input");
		channel.send(MessageBuilder.withPayload(file.getBytes()).build());
		return "redirect:/";
	}

	public static void main(String[] args) {
		new AggregateApplicationBuilder(IngesterApplication.class, args)
				.from(FileSourceApplication.class).to(IngesterProcessor.class)
				.namespace("ingester").run();
	}
}
