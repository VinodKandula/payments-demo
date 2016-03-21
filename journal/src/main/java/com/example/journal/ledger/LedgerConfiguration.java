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

package com.example.journal.ledger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.journal.Journal;
import com.example.journal.JournalEntry;

/**
 * @author Dave Syer
 *
 */
@Configuration
@EnableBinding(JournalSink.class)
public class LedgerConfiguration {

	@Bean
	public InitializingBean ledgerSink(JournalSink sink, Journal journal) {
		return () -> {
			sink.input().subscribe(message -> {
				JournalEntry entry = (JournalEntry) message.getPayload();
				String type = (String) message.getHeaders().get("X-Journal-Type");
				switch (type.toLowerCase()) {
				case "debit":
					journal.debit(entry);
					break;
				case "credit":
					journal.credit(entry);
					break;
				default:
					throw new IllegalStateException("Unsupported journal type: " + type);
				}
			});
		};
	}

}
