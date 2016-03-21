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

package com.example.journal;

import org.joda.money.Money;

/**
 * @author Dave Syer
 *
 */
public class JournalEntry {

	private String account;

	private String source;

	private String transactionId;

	private long timestamp;

	private Money amount;

	JournalEntry() {
	}

	public JournalEntry(String account, String source, String transactionId,
			Money amount) {
		this(account, source, transactionId, amount, System.currentTimeMillis());
	}

	public JournalEntry(String account, String source, String transactionId, Money amount,
			long timestamp) {
		this.account = account;
		this.source = source;
		this.transactionId = transactionId;
		this.timestamp = timestamp;
		this.amount = amount;
	}

	public String getAccount() {
		return account;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Money getAmount() {
		return amount;
	}

	public String getSource() {
		return source;
	}

	@Override
	public String toString() {
		return "JournalEntry [account=" + account + ", source=" + source
				+ ", transactionId=" + transactionId + ", timestamp=" + timestamp
				+ ", amount=" + amount + "]";
	}

}
