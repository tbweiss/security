package com.tbw.security.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tbw.security.model.Event;
import com.tbw.security.publisher.EventPublisher;
import com.tbw.security.utils.EventSerDes;

public class App implements EventPublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		new App().proceed();
	}

	public void proceed() throws Exception {
		Event event = Event.newBuilder()
				.setId("evt-1")
				.setTimestamp(System.currentTimeMillis())
				.setMessage("Hello from Avro")
				.build()
		;

		publish(event);
	}
}
