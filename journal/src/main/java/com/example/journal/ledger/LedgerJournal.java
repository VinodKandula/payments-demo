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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.journal.Journal;
import com.example.journal.JournalEntry;

/**
 * @author Dave Syer
 *
 */
public class LedgerJournal implements Journal {

	private static Logger log = LoggerFactory.getLogger(LedgerJournal.class);

	@Override
	public void debit(JournalEntry entry) {
		// TODO: ship data out to eventually consistent global store
		log.info("Debit: " + entry);
	}

	@Override
	public void credit(JournalEntry entry) {
		log.info("Cebit: " + entry);
	}

}
