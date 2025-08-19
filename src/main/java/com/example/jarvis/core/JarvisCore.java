package com.example.jarvis.core;

import com.example.jarvis.calendar.CalendarService;
import com.example.jarvis.calendar.Reminder;
import com.example.jarvis.humor.HumorService;
import com.example.jarvis.model.CommandResponse;
import com.example.jarvis.model.Intent;
import com.example.jarvis.model.ParsedCommand;
import com.example.jarvis.nlp.NlpService;
import com.example.jarvis.search.InfoSearchService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class JarvisCore {

    private final NlpService nlpService;
    private final CalendarService calendarService;
    private final HumorService humorService;
    private final InfoSearchService infoSearchService;

    public JarvisCore(NlpService nlpService, CalendarService calendarService, HumorService humorService, InfoSearchService infoSearchService) {
        this.nlpService = nlpService;
        this.calendarService = calendarService;
        this.humorService = humorService;
        this.infoSearchService = infoSearchService;
    }

    public CommandResponse processCommand(String input) {
        ParsedCommand parsed = nlpService.parse(input);
        Intent intent = parsed.getIntent();

        switch (intent) {
            case ADD_REMINDER:
                Reminder reminder = calendarService.addReminder(parsed.getWhen(), parsed.getTitle());
                String whenStr = reminder.getWhen().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                String reply = humorService.decorate("Записал напоминание: '" + reminder.getTitle() + "' на " + whenStr);
                Map<String, Object> data = new HashMap<>();
                data.put("id", reminder.getId());
                data.put("when", whenStr);
                return new CommandResponse(reply, Intent.ADD_REMINDER.name(), data);
            case SMALL_TALK:
                return new CommandResponse(humorService.decorate("Работаю в штатном режиме"), Intent.SMALL_TALK.name(), null);
            case WEB_SEARCH:
                String query = parsed.getOriginalText()
                        .replaceFirst("(?i)^(поиск|найди|что такое|кто такой|кто такая|search|find|what is|who is)\\s+", "")
                        .trim();
                var results = infoSearchService.searchWeb(query, 3);
                var top = results.stream().findFirst();
                String base = top.map(r -> "Нашёл: " + (r.title() != null ? r.title() : r.link()) + ". " + (r.snippet() != null ? r.snippet() : ""))
                        .orElse("Ничего подходящего не нашёл");
                return new CommandResponse(humorService.decorate(base), Intent.WEB_SEARCH.name(), Map.of("results", results));
            default:
                return new CommandResponse(humorService.decorate("Я понял команду как-то туманно. Сформулируйте иначе"), Intent.UNKNOWN.name(), null);
        }
    }
}


