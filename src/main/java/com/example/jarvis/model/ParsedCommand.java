package com.example.jarvis.model;

import java.time.LocalDateTime;

public class ParsedCommand {
    private Intent intent;
    private String title;
    private LocalDateTime when;
    private String originalText;

    public ParsedCommand() {}

    public ParsedCommand(Intent intent, String title, LocalDateTime when, String originalText) {
        this.intent = intent;
        this.title = title;
        this.when = when;
        this.originalText = originalText;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getWhen() {
        return when;
    }

    public void setWhen(LocalDateTime when) {
        this.when = when;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }
}



