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

import java.io.Flushable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import com.librato.metrics.BatchResult;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.Sanitizer;

/**
 * @author Dave Syer
 *
 */
public class LibratoMetricWriter implements MetricWriter, Flushable {

	private Logger log = LoggerFactory.getLogger(LibratoMetricWriter.class);

	private LibratoBatch batch;
	private final LibratoProperties librato;
	private final HttpPoster poster;

	public LibratoMetricWriter(HttpPoster poster, LibratoProperties librato) {
		this.poster = poster;
		this.librato = librato;
		this.batch = new LibratoBatch(librato.getBatchSize(), Sanitizer.NO_OP,
				librato.getTimeoutMillis(), TimeUnit.MILLISECONDS, librato.getAgent(),
				poster);
	}

	@Override
	public void set(Metric<?> value) {
		batch.addGaugeMeasurement(value.getName(), value.getValue());
	}

	@Override
	public void increment(Delta<?> delta) {
		batch.addCounterMeasurement(delta.getName(), delta.getValue().longValue());
	}

	@Override
	public void reset(String metricName) {
		// no-op
	}

	@Override
	public void flush() throws IOException {
		try {
			BatchResult result = batch.post(librato.getSource(),
					System.currentTimeMillis() / 1000);
			if (!result.success()) {
				log.warn("Could not post {} (out of {}) batches to librato",
						result.getFailedPosts().size(), result.getPosts().size());
				if (!result.getFailedPosts().isEmpty()) {
					log.debug("Failed batch: {}", result.getFailedPosts().get(0));
				}
			}
			if (!result.getPosts().isEmpty()) {
				this.batch = new LibratoBatch(librato.getBatchSize(), Sanitizer.NO_OP,
						librato.getTimeoutMillis(), TimeUnit.MILLISECONDS,
						librato.getAgent(), poster);
			}
		}
		catch (

		Exception e)

		{
			log.warn("Cannot post to Librato");
		}
	}

}
