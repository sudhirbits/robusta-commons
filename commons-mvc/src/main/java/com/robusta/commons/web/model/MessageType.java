package com.robusta.commons.web.model;


public enum MessageType {
    INFO, ERROR, WARN, FATAL;

    public String value(){
        return name();
    }

    public static MessageType fromValue(String name){
        return valueOf(name);
    }
}
