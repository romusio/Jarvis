package com.example.jarvis.calendar;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {
    Reminder addReminder(LocalDateTime when, String title);
    List<Reminder> getReminders();
}



