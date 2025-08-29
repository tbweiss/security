package com.tbw.security.publisher;

import com.tbw.security.model.Event;
import com.tbw.security.utils.EventSerDes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface EventPublisher {
    Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    default void publish(Event event) throws Exception {
		String jsonString = EventSerDes.serializeToJsonString(event);
        logger.info("Publishing event: {}", jsonString);
    }
}
