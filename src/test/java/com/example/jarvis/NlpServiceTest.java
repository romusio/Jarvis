package com.example.jarvis;

import com.example.jarvis.model.Intent;
import com.example.jarvis.model.ParsedCommand;
import com.example.jarvis.nlp.NlpService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NlpServiceTest {

    private final NlpService nlp = new NlpService();

    @Test
    void ruRelativeInMinutes() {
        ParsedCommand p = nlp.parse("Напомни мне позвонить маме через 15 минут");
        assertEquals(Intent.ADD_REMINDER, p.getIntent());
        assertTrue(p.getTitle().toLowerCase().contains("позвонить маме"));
        assertNotNull(p.getWhen());
    }

    @Test
    void enRelativeInHours() {
        ParsedCommand p = nlp.parse("Remind me to send report in 2 hours");
        assertEquals(Intent.ADD_REMINDER, p.getIntent());
        assertTrue(p.getTitle().toLowerCase().contains("send report"));
        assertNotNull(p.getWhen());
    }

    @Test
    void ruDayAfterTomorrow() {
        ParsedCommand p = nlp.parse("Напомни подготовить презентацию послезавтра в 9:30");
        assertEquals(Intent.ADD_REMINDER, p.getIntent());
        assertTrue(p.getTitle().toLowerCase().contains("подготовить презентацию"));
        assertNotNull(p.getWhen());
    }
}





