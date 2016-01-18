package com.nearsoft.nearbooks.ws.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Message response.
 * Created by epool on 1/13/16.
 */
public class MessageResponse {

    @SerializedName("id")
    private int messageId;

    @SerializedName("message")
    private String message;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
