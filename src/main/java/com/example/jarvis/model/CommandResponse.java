package com.example.jarvis.model;

import java.util.Map;

public class CommandResponse {
    private String reply;
    private String intent;
    private Map<String, Object> data;

    public CommandResponse() {}

    public CommandResponse(String reply, String intent, Map<String, Object> data) {
        this.reply = reply;
        this.intent = intent;
        this.data = data;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}


