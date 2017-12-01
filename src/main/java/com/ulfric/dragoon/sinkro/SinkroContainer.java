package com.ulfric.dragoon.sinkro;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.ulfric.acrodb.Bucket;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.reflect.Classes;
import com.ulfric.sinkro.Sink;

public class SinkroContainer extends Container {

	@Inject
	private ObjectFactory factory;

	public SinkroContainer() {
		addBootHook(this::bindSinkro);
		addShutdownHook(this::unbindSinkro);
	}

	private void bindSinkro() {
		factory.bind(Sink.class).toFunction(parameters -> {
			Bucket bucket = factory.request(Bucket.class, parameters);

			return new Sink<>(bucket, getSinkType(parameters.getQualifier().getType()));
		});
	}

	private Class<?> getSinkType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] arguments = parameterizedType.getActualTypeArguments();
			if (arguments.length > 0) {
				return Classes.getRawType(arguments[0]);
			}
		}

		throw new IllegalStateException("Could not find sink type from " + type);
	}

	private void unbindSinkro() {
		factory.bind(Sink.class).toNothing();
	}

}
