package com.example.jarvis.calendar;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnProperty(name = "jarvis.calendar.provider", havingValue = "google")
public class GoogleCalendarService implements CalendarService {

    private final String timezone;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public GoogleCalendarService(@Value("${jarvis.calendar.timezone:UTC}") String timezone) {
        this.timezone = timezone;
    }

    @Override
    public Reminder addReminder(LocalDateTime when, String title) {
        try {
            String accessToken = fetchAccessToken();
            ZonedDateTime start = when.atZone(ZoneId.of(timezone));
            ZonedDateTime end = start.plusMinutes(30);
            String body = "{"
                    + "\"summary\":\"" + escapeJson(title) + "\"," 
                    + "\"start\":{\"dateTime\":\"" + start.toOffsetDateTime() + "\",\"timeZone\":\"" + timezone + "\"},"
                    + "\"end\":{\"dateTime\":\"" + end.toOffsetDateTime() + "\",\"timeZone\":\"" + timezone + "\"}"
                    + "}";
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.googleapis.com/calendar/v3/calendars/primary/events"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return new Reminder(title, when);
        } catch (Exception e) {
            return new Reminder(title, when);
        }
    }

    @Override
    public List<Reminder> getReminders() {
        // Упростим: список не используется в MVP с Google, можно дополнить позже
        return Collections.emptyList();
    }

    private String fetchAccessToken() throws Exception {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/calendar"));
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


