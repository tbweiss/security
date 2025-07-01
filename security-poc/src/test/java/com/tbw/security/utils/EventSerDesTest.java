package com.tbw.security.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.tbw.security.model.Event;

public class EventSerDesTest {
    @Test
    void testDeserialize() throws Exception {
        Event event = Event.newBuilder()
                .setId("evt-1")
                .setTimestamp(123456789L)
                .setMessage("Hello from Avro")
                .build();
        byte[] data = EventSerDes.serialize(event);
        Event result = EventSerDes.deserialize(data);
        assertEquals(event.getId().toString(), result.getId().toString());
        assertEquals(event.getTimestamp(), result.getTimestamp());
        assertEquals(event.getMessage().toString(), result.getMessage().toString());
    }

    @Test
    void testSerialize() throws Exception {
        Event event = Event.newBuilder()
                .setId("evt-1")
                .setTimestamp(123456789L)
                .setMessage("Hello from Avro")
                .build();
        byte[] data = EventSerDes.serialize(event);
        assertNotNull(data);
        assertTrue(data.length > 0);
    }

    @Test
    void testSerializeToJsonString() throws Exception {
        Event event = Event.newBuilder()
                .setId("evt-1")
                .setTimestamp(123456789L)
                .setMessage("Hello from Avro")
                .build();
        String json = EventSerDes.serializeToJsonString(event);
        assertNotNull(json);
        assertTrue(json.contains("evt-1"));
        assertTrue(json.contains("Hello from Avro"));
    }
}
