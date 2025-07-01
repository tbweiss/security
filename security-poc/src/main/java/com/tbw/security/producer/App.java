package com.tbw.security.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tbw.security.model.Event;
import com.tbw.security.utils.EventSerDes;

public class App {
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

		LOGGER.info("Created event: {}", event.getId());

		byte[] serializedEvent = EventSerDes.serialize(event);
		LOGGER.info("Serialized event to byte array: {}", serializedEvent);	

		Event deserializedEvent = EventSerDes.deserialize(serializedEvent);
		LOGGER.info("Deserialized event: {}", deserializedEvent.getId());	

		String jsonString = EventSerDes.serializeToJsonString(event);
		LOGGER.info("Serialized event to JSON string: {}", jsonString);
	}
}
