package com.robusta.commons.async.defaults.jobops;

import com.robusta.commons.async.api.SerializationOperations;
import com.thoughtworks.xstream.XStream;
import org.springframework.stereotype.Component;

@Component
public class XStreamSerializationOperations<Serializable> implements SerializationOperations<Serializable> {
    private final XStream xstream;

    public XStreamSerializationOperations() {
        this.xstream = new XStream();
    }

    @Override
    public String toString(Serializable toBeSerialized) {
        return xstream.toXML(toBeSerialized);
    }

    @Override
    public Serializable fromString(String serialized) {
        return (Serializable) xstream.fromXML(serialized);
    }
}
