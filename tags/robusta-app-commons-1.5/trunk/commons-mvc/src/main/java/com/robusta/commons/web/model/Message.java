package com.robusta.commons.web.model;

import java.io.Serializable;

public class Message implements Serializable {
    protected MessageType messageType;
    protected String message;

    private Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public static Message info(String message) {
        return new Message(MessageType.INFO).withMessage(message);
    }

    public static Message error(String message) {
        return new Message(MessageType.ERROR).withMessage(message);
    }

    public static Message warning(String message) {
        return new Message(MessageType.WARN).withMessage(message);
    }

    public static Message fatal(String message) {
        return new Message(MessageType.FATAL).withMessage(message);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    private Message withMessage(String message) {
        this.message = message;
        return this;
    }
}
