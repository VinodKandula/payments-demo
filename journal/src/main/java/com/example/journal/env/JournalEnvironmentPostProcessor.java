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

package com.example.journal.env;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import com.example.journal.JournalEntry;
import com.example.journal.ledger.JournalSink;
import com.example.journal.teller.JournalSource;

/**
 * @author Dave Syer
 *
 */
public class JournalEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final String PROPERTY_SOURCE_NAME = "defaultProperties";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment,
			SpringApplication application) {
		Map<String, Object> map = new HashMap<String, Object>();
		// TODO: Spring Cloud Stream is changing to *.bindings.*.producer.*
		map.put("spring.cloud.stream.bindings." + JournalSource.OUTPUT
				+ ".required-groups",
				environment.getProperty("journal.stream.group", "default"));
		map.put("spring.cloud.stream.bindings." + JournalSink.INPUT + ".group",
				environment.getProperty("journal.stream.group", "default"));
		map.put("spring.cloud.stream.bindings." + JournalSource.OUTPUT + ".destination",
				environment.getProperty("journal.destination", "journal"));
		map.put("spring.cloud.stream.bindings." + JournalSink.INPUT + ".destination",
				environment.getProperty("journal.destination", "journal"));
		map.put("spring.cloud.stream.bindings." + JournalSource.OUTPUT + ".content-type",
				environment.getProperty("journal.content-type", "application/json"));
		map.put("spring.cloud.stream.bindings." + JournalSink.INPUT + ".content-type",
				"application/x-java-object;type=" + JournalEntry.class.getName());
		addOrReplace(environment.getPropertySources(), map);
	}

	private void addOrReplace(MutablePropertySources propertySources,
			Map<String, Object> map) {
		MapPropertySource target = null;
		if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
			PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
			if (source instanceof MapPropertySource) {
				target = (MapPropertySource) source;
				for (String key : map.keySet()) {
					if (!target.containsProperty(key)) {
						target.getSource().put(key, map.get(key));
					}
				}
			}
		}
		if (target == null) {
			target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
		}
		if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
			propertySources.addLast(target);
		}
	}

}