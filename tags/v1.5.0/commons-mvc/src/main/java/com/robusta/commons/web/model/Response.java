package com.robusta.commons.web.model;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Response implements Serializable {
    protected boolean error;
    protected List<Message> messageList;

    protected Response() {}

    private Response(boolean failure) {
        this.error = failure;
        this.messageList = newArrayList();
    }

    public static Response success() {
        return new Response(false);
    }

    public static Response failure() {
        return new Response(true);
    }
    public boolean isError() {
        return error;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public Response withMessage(Message... messages){
        for (Message message : messages) {
           messageList.add(message);
        }
        return this;
    }
}
