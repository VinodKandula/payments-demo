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

package com.example.common;

import org.springframework.cloud.stream.converter.AbstractFromMessageConverter;
import org.springframework.cloud.stream.converter.MessageConverterUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Dave Syer
 *
 */
@Configuration
// TODO: make the streamy bits conditional
public class MoneyJacksonAutoConfiguration {

	@Bean
	public JodaMoneyModule jodaMoneyJacksonModule() {
		return new JodaMoneyModule();
	}

	@Bean
	public CustomJsonToPojoMessageConverter journalJacksonMessageConverter(ObjectMapper mapper) {
		return new CustomJsonToPojoMessageConverter(mapper);
	}

	@Bean
	public CustomPojoToJsonMessageConverter JournalJacksonSerializerMessageConverter(ObjectMapper mapper) {
		return new CustomPojoToJsonMessageConverter(mapper);
	}

}

class CustomPojoToJsonMessageConverter extends AbstractFromMessageConverter {

	private final ObjectMapper mapper;

	public CustomPojoToJsonMessageConverter(ObjectMapper mapper) {
		super(MimeTypeUtils.APPLICATION_JSON);
		this.mapper = mapper;
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.registerModule(new JodaMoneyModule());
	}

	@Override
	protected Class<?>[] supportedPayloadTypes() {
		return null;
	}

	@Override
	protected Class<?>[] supportedTargetTypes() {
		return new Class[] { String.class };
	}

	@Override
	public Object convertFromInternal(Message<?> message, Class<?> targetClass,
			Object conversionHint) {
		try {
			Object payload = message.getPayload();
			return mapper.writeValueAsString(payload);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}

class CustomJsonToPojoMessageConverter extends AbstractFromMessageConverter {

	private final ObjectMapper mapper;

	public CustomJsonToPojoMessageConverter(ObjectMapper mapper) {
		super(MimeTypeUtils.APPLICATION_JSON, MessageConverterUtils.X_JAVA_OBJECT);
		this.mapper = mapper;
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.registerModule(new JodaMoneyModule());
	}

	@Override
	protected Class<?>[] supportedPayloadTypes() {
		return new Class<?>[] { String.class, byte[].class };
	}

	@Override
	protected Class<?>[] supportedTargetTypes() {
		return null; // any type
	}

	@Override
	public Object convertFromInternal(Message<?> message, Class<?> targetClass,
			Object conversionHint) {
		Object result = null;
		try {
			Object payload = message.getPayload();

			if (payload instanceof byte[]) {
				result = mapper.readValue((byte[]) payload, targetClass);
			}
			else if (payload instanceof String) {
				result = mapper.readValue((String) payload, targetClass);
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return result;
	}

}

@SuppressWarnings("serial")
class SubtypeModule extends SimpleModule {
	private Class<?>[] subtypes;

	public SubtypeModule(Class<?>... subtypes) {
		this.subtypes = subtypes;
	}

	@Override
	public void setupModule(SetupContext context) {
		context.registerSubtypes(subtypes);
		super.setupModule(context);
	}
}
