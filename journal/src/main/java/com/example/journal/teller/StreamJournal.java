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

package com.example.journal.teller;

import org.springframework.messaging.support.MessageBuilder;

import com.example.journal.Journal;
import com.example.journal.JournalEntry;

/**
 * @author Dave Syer
 *
 */
public class StreamJournal implements Journal {

	private final JournalSource source;

	public StreamJournal(JournalSource source) {
		this.source = source;
	}

	@Override
	public void debit(JournalEntry entry) {
		source.output().send(MessageBuilder.withPayload(entry)
				.setHeader("X-Journal-Type", "debit").build());
	}

	@Override
	public void credit(JournalEntry entry) {
		source.output().send(MessageBuilder.withPayload(entry)
				.setHeader("X-Journal-Type", "credit").build());
	}

}
