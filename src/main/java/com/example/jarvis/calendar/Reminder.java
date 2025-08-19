package com.example.jarvis.calendar;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reminder {
    private final String id;
    private final String title;
    private final LocalDateTime when;

    public Reminder(String title, LocalDateTime when) {
        this(UUID.randomUUID().toString(), title, when);
    }

    public Reminder(String id, String title, LocalDateTime when) {
        this.id = id;
        this.title = title;
        this.when = when;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getWhen() {
        return when;
    }
}



