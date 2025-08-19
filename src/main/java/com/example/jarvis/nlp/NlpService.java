package com.example.jarvis.nlp;

import com.example.jarvis.model.Intent;
import com.example.jarvis.model.ParsedCommand;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NlpService {

    private static final Pattern RU_TOMORROW_TIME = Pattern.compile("(?i)завтра\\s*в\\s*(\\d{1,2})(?::(\\d{2}))?");
    private static final Pattern EN_TOMORROW_TIME = Pattern.compile("(?i)tomorrow\\s*at\\s*(\\d{1,2})(?::(\\d{2}))?");

    private static final Pattern RU_TODAY_TIME = Pattern.compile("(?i)сегодня\\s*в\\s*(\\d{1,2})(?::(\\d{2}))?");
    private static final Pattern EN_TODAY_TIME = Pattern.compile("(?i)today\\s*at\\s*(\\d{1,2})(?::(\\d{2}))?");

    private static final Pattern RU_DAY_AFTER_TMRW_TIME = Pattern.compile("(?i)послезавтра\\s*в\\s*(\\d{1,2})(?::(\\d{2}))?");
    private static final Pattern EN_DAY_AFTER_TMRW_TIME = Pattern.compile("(?i)(day after tomorrow|the day after tomorrow)\\s*at\\s*(\\d{1,2})(?::(\\d{2}))?");

    private static final Pattern RU_IN_DURATION = Pattern.compile("(?i)через\\s+(\\d+)\\s*(минут(?:у|ы|)?|час(?:а|ов)?|дн(?:я|ей|ень)?)");
    private static final Pattern EN_IN_DURATION = Pattern.compile("(?i)in\\s+(\\d+)\\s*(minutes?|hours?|days?)");

    private static final Pattern RU_AT_TIME = Pattern.compile("(?i)в\\s*(\\d{1,2})(?::(\\d{2}))?");
    private static final Pattern EN_AT_TIME = Pattern.compile("(?i)at\\s*(\\d{1,2})(?::(\\d{2}))?");

    public ParsedCommand parse(String text) {
        if (text == null || text.isBlank()) {
            return new ParsedCommand(Intent.UNKNOWN, null, null, text);
        }
        String normalized = text.trim();
        String lower = normalized.toLowerCase(Locale.ROOT);

        // Heuristics only if we have a trigger word
        boolean ruTrigger = lower.contains("напомни");
        boolean enTrigger = lower.contains("remind");

        // RU: relative duration "через X ..."
        if (ruTrigger) {
            Matcher m = RU_IN_DURATION.matcher(lower);
            if (m.find()) {
                LocalDateTime when = computeInDuration(m.group(1), m.group(2));
                String title = extractTitleBeforeAnchors(normalized, "напомни", new String[]{"через", "сегодня", "завтра", "послезавтра"});
                if (title == null || title.isBlank()) title = "задачу";
                return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
            }
        }

        // EN: relative duration "in X ..."
        if (enTrigger) {
            Matcher m = EN_IN_DURATION.matcher(lower);
            if (m.find()) {
                LocalDateTime when = computeInDuration(m.group(1), m.group(2));
                String title = extractTitleBeforeAnchors(normalized, "remind", new String[]{"in", "today", "tomorrow", "day after tomorrow", "the day after tomorrow"});
                if (title == null || title.isBlank()) title = "task";
                return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
            }
        }

        // RU: today/tomorrow/day after tomorrow with time
        if (ruTrigger && lower.contains("сегодня")) {
            LocalDateTime when = extractTimeWithDayOffset(lower, RU_TODAY_TIME, 0);
            String title = extractTitle(normalized, "напомни", "сегодня");
            if (title == null || title.isBlank()) title = "задачу";
            return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
        }
        if (ruTrigger && lower.contains("завтра")) {
            LocalDateTime when = extractTimeWithDayOffset(lower, RU_TOMORROW_TIME, 1);
            String title = extractTitle(normalized, "напомни", "завтра");
            if (title == null || title.isBlank()) title = "задачу";
            return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
        }
        if (ruTrigger && lower.contains("послезавтра")) {
            LocalDateTime when = extractTimeWithDayOffset(lower, RU_DAY_AFTER_TMRW_TIME, 2);
            String title = extractTitle(normalized, "напомни", "послезавтра");
            if (title == null || title.isBlank()) title = "задачу";
            return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
        }

        // EN: today/tomorrow/day after tomorrow with time
        if (enTrigger && lower.contains("today")) {
            LocalDateTime when = extractTimeWithDayOffset(lower, EN_TODAY_TIME, 0);
            String title = extractTitle(normalized, "remind", "today");
            if (title == null || title.isBlank()) title = "task";
            return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
        }
        if (enTrigger && lower.contains("tomorrow")) {
            LocalDateTime when = extractTimeWithDayOffset(lower, EN_TOMORROW_TIME, 1);
            String title = extractTitle(normalized, "remind", "tomorrow");
            if (title == null || title.isBlank()) title = "task";
            return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
        }
        if (enTrigger && (lower.contains("day after tomorrow") || lower.contains("the day after tomorrow"))) {
            LocalDateTime when = extractTimeWithDayOffset(lower, EN_DAY_AFTER_TMRW_TIME, 2);
            String anchor = lower.contains("the day after tomorrow") ? "the day after tomorrow" : "day after tomorrow";
            String title = extractTitle(normalized, "remind", anchor);
            if (title == null || title.isBlank()) title = "task";
            return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
        }

        // If only time provided with trigger, assume today or next day
        if (ruTrigger) {
            Matcher tm = RU_AT_TIME.matcher(lower);
            if (tm.find()) {
                LocalDateTime when = resolveTodayOrTomorrow(tm.group(1), tm.group(2));
                String title = extractTitleBeforeAnchors(normalized, "напомни", new String[]{"в"});
                if (title == null || title.isBlank()) title = "задачу";
                return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
            }
        }
        if (enTrigger) {
            Matcher tm = EN_AT_TIME.matcher(lower);
            if (tm.find()) {
                LocalDateTime when = resolveTodayOrTomorrow(tm.group(1), tm.group(2));
                String title = extractTitleBeforeAnchors(normalized, "remind", new String[]{"at"});
                if (title == null || title.isBlank()) title = "task";
                return new ParsedCommand(Intent.ADD_REMINDER, title, when, text);
            }
        }

        // Small talk heuristic
        if (lower.matches("(?s).*(как дела|привет|hi|hello|кто ты|что умеешь).*$")) {
            return new ParsedCommand(Intent.SMALL_TALK, null, null, text);
        }

        // Web search heuristics
        if (lower.startsWith("поиск ") || lower.startsWith("найди ") || lower.startsWith("что такое ")
                || lower.startsWith("кто такой ") || lower.startsWith("кто такая ")
                || lower.startsWith("search ") || lower.startsWith("find ") || lower.startsWith("what is ") || lower.startsWith("who is ")) {
            return new ParsedCommand(Intent.WEB_SEARCH, text.trim(), null, text);
        }

        return new ParsedCommand(Intent.UNKNOWN, null, null, text);
    }

    private LocalDateTime extractTimeTomorrow(String lower, Pattern pattern) {
        Matcher matcher = pattern.matcher(lower);
        int hour = 10;
        int minute = 0;
        if (matcher.find()) {
            try {
                hour = Integer.parseInt(matcher.group(1));
                if (matcher.groupCount() >= 2 && matcher.group(2) != null) {
                    minute = Integer.parseInt(matcher.group(2));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(Math.min(Math.max(hour, 0), 23), Math.min(Math.max(minute, 0), 59));
        return LocalDateTime.of(date, time);
    }

    private LocalDateTime extractTimeWithDayOffset(String lower, Pattern pattern, int dayOffset) {
        Matcher matcher = pattern.matcher(lower);
        int hour = 10;
        int minute = 0;
        if (matcher.find()) {
            try {
                hour = Integer.parseInt(matcher.group(1));
                if (matcher.groupCount() >= 2 && matcher.group(2) != null) {
                    minute = Integer.parseInt(matcher.group(2));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        LocalDate date = LocalDate.now().plusDays(dayOffset);
        LocalTime time = LocalTime.of(Math.min(Math.max(hour, 0), 23), Math.min(Math.max(minute, 0), 59));
        return LocalDateTime.of(date, time);
    }

    private LocalDateTime resolveTodayOrTomorrow(String hourStr, String minuteStr) {
        int hour = 10;
        int minute = 0;
        try {
            hour = Integer.parseInt(hourStr);
            if (minuteStr != null) minute = Integer.parseInt(minuteStr);
        } catch (NumberFormatException ignored) {}
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(Math.min(Math.max(hour, 0), 23), Math.min(Math.max(minute, 0), 59));
        LocalDateTime candidate = LocalDateTime.of(date, time);
        if (candidate.isBefore(LocalDateTime.now())) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private String extractTitle(String original, String trigger, String anchor) {
        String lower = original.toLowerCase(Locale.ROOT);
        int startIdx = lower.indexOf(trigger);
        int anchorIdx = lower.indexOf(anchor);
        if (startIdx == -1 || anchorIdx == -1 || anchorIdx <= startIdx) {
            return null;
        }
        // Remove the trigger word and any filler like "мне" / "me to"
        String between = original.substring(startIdx + trigger.length(), anchorIdx).trim();
        between = between.replaceFirst("^(мне|me|me to|мне\n)\\s*", "").replaceFirst("^to\\s+", "");
        return between.isBlank() ? null : between;
    }

    private String extractTitleBeforeAnchors(String original, String trigger, String[] anchors) {
        String lower = original.toLowerCase(Locale.ROOT);
        int startIdx = lower.indexOf(trigger);
        if (startIdx == -1) return null;
        int best = -1;
        for (String a : anchors) {
            int idx = lower.indexOf(a, startIdx + trigger.length());
            if (idx != -1 && (best == -1 || idx < best)) best = idx;
        }
        if (best == -1) return null;
        String between = original.substring(startIdx + trigger.length(), best).trim();
        between = between.replaceFirst("^(мне|me|me to|to)\\s+", "");
        return between.isBlank() ? null : between;
    }

    private LocalDateTime computeInDuration(String numStr, String unitStr) {
        int amount = 0;
        try { amount = Integer.parseInt(numStr); } catch (NumberFormatException ignored) {}
        amount = Math.max(0, amount);
        String unit = unitStr.toLowerCase(Locale.ROOT);
        LocalDateTime now = LocalDateTime.now();
        if (unit.startsWith("минут")) {
            return now.plusMinutes(amount);
        } else if (unit.startsWith("час")) {
            return now.plusHours(amount);
        } else if (unit.startsWith("д")) { // день/дня/дней
            return now.plusDays(amount);
        } else if (unit.startsWith("minute")) {
            return now.plusMinutes(amount);
        } else if (unit.startsWith("hour")) {
            return now.plusHours(amount);
        } else if (unit.startsWith("day")) {
            return now.plusDays(amount);
        }
        return now.plusHours(1);
    }
}


