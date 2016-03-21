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

import java.io.IOException;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author Dave Syer
 *
 */
@SuppressWarnings("serial")
public class JodaMoneyModule extends SimpleModule {
	
	public JodaMoneyModule() {
		addDeserializer(Money.class, new MoneyDeserializer());
		addSerializer(Money.class, new MoneySerializer());
	}

	private static class MoneySerializer extends StdSerializer<Money> {
		protected MoneySerializer() {
			super(Money.class);
		}

		@Override
		public void serialize(Money value, JsonGenerator generator,
				SerializerProvider provider) throws IOException {
			generator.writeString(value.toString());
		}
	}

	private static class MoneyDeserializer extends StdDeserializer<Money> {
		protected MoneyDeserializer() {
			super(Money.class);
		}

		@Override
		public Money deserialize(JsonParser parser, DeserializationContext context)
				throws IOException {
			return Money.parse(parser.readValueAs(String.class));
		}
	}

}