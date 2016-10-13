package com.cuber.zkweb.model;

/**
 * Created by cuber on 2016/10/13.
 */
public class ResponseMessage {

    private boolean done;

    private String transJsonValue;

    private String message;

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransJsonValue() {
        return transJsonValue;
    }

    public void setTransJsonValue(String transJsonValue) {
        this.transJsonValue = transJsonValue;
    }
}
