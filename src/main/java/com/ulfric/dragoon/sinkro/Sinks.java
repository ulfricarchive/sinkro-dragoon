package com.ulfric.dragoon.sinkro;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ulfric.acrodb.Bucket;
import com.ulfric.sinkro.Sink;

public final class Sinks<T> {

	private final ConcurrentMap<String, Sink<T>> sinks = new ConcurrentHashMap<>(2);
	private final Bucket bucket;
	private final Class<T> type;

	public Sinks(Bucket bucket, Class<T> type) {
		Objects.requireNonNull(bucket, "bucket");
		Objects.requireNonNull(type, "type");

		this.bucket = bucket;
		this.type = type;
	}

	public Sink<T> openSink(String name) {
		return sinks.computeIfAbsent(name, key -> {
			return new Sink<>(bucket.openDocument(name), type);
		});
	}

}
