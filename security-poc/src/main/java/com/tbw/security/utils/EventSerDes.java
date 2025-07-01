package com.tbw.security.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import com.tbw.security.model.Event;

public class EventSerDes {

    public static byte[] serialize(Event event) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // This BinaryEncoder is not thread-safe, so it should not be reused across threads.
        BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        DatumWriter<Event> datumWriter = new SpecificDatumWriter<Event>(Event.getClassSchema());
        datumWriter.write(event, binaryEncoder);
        binaryEncoder.flush();
        return outputStream.toByteArray();
    }

    public static Event deserialize(byte[] data) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(inputStream, null);
        DatumReader<Event> datumReader = new SpecificDatumReader<Event>(Event.getClassSchema());
        return datumReader.read(null, binaryDecoder);
    }

    public static String serializeToJsonString(Event event) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(Event.getClassSchema(), outputStream);
        DatumWriter<Event> datumWriter = new SpecificDatumWriter<Event>(Event.getClassSchema());
        datumWriter.write(event, jsonEncoder);
        jsonEncoder.flush();
        return outputStream.toString("UTF-8");
    }

}
