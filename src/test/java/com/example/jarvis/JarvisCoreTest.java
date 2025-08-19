package com.example.jarvis;

import com.example.jarvis.core.JarvisCore;
import com.example.jarvis.model.CommandResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JarvisCoreTest {

    @Autowired
    private JarvisCore jarvisCore;

    @Test
    void testReminderIntentRussian() {
        CommandResponse response = jarvisCore.processCommand("Джарвис, напомни позвонить маме завтра в 10");
        assertNotNull(response);
        assertTrue(response.getReply().toLowerCase().contains("напоминание"));
    }
}





