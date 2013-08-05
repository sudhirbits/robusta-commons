package com.robusta.commons.async.api;

public interface SerializationOperations<Serializable> {
    public String toString(Serializable toBeSerialized);
    public Serializable fromString(String serialized);
}
