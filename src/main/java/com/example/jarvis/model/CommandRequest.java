package com.example.jarvis.model;

import jakarta.validation.constraints.NotBlank;

public class CommandRequest {

    @NotBlank
    private String text;

    public CommandRequest() {
    }

    public CommandRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


