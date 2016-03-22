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

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.journal.Journal;
import com.example.journal.JournalEntry;

/**
 * @author Dave Syer
 *
 */
public class OnceOnlyJournal implements Journal {
	
	private static Logger log = LoggerFactory.getLogger(OnceOnlyJournal.class);
	
	private Set<String> transactions = new ConcurrentSkipListSet<>();
	
	private Journal delegate;

	public OnceOnlyJournal(Journal delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean debit(JournalEntry entry) {
		if (transactions.contains(entry.getTransactionId())) {
			log.warn("Duplicate debit entry: " + entry);
			return false;
		}
		transactions.add(entry.getTransactionId());
		return delegate.debit(entry);
	}

	@Override
	public boolean credit(JournalEntry entry) {
		if (transactions.contains(entry.getTransactionId())) {
			log.warn("Duplicate credit entry: " + entry);
			return false;
		}
		transactions.add(entry.getTransactionId());
		return delegate.credit(entry);
	}

}
