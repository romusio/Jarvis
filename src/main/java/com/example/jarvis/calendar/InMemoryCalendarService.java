package com.example.jarvis.calendar;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@ConditionalOnProperty(name = "jarvis.calendar.provider", havingValue = "memory", matchIfMissing = true)
public class InMemoryCalendarService implements CalendarService {

    private final CopyOnWriteArrayList<Reminder> reminders = new CopyOnWriteArrayList<>();

    @Override
    public Reminder addReminder(LocalDateTime when, String title) {
        Reminder reminder = new Reminder(title, when);
        reminders.add(reminder);
        return reminder;
    }

    @Override
    public List<Reminder> getReminders() {
        return Collections.unmodifiableList(reminders);
    }
}


