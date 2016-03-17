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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Dave Syer
 *
 */
@ConfigurationProperties("librato")
public class LibratoProperties {
	private String email = "noone@example.com";
	private String token;
	private String url = "https://metrics-api.librato.com/v1/metrics";
	private String agent = "spring.boot";
	@Value("${spring.application.name:application}")
	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	private int batchSize = 300;
	private long timeoutMillis = 2000;
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public long getTimeoutMillis() {
		return timeoutMillis;
	}
	public void setTimeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
