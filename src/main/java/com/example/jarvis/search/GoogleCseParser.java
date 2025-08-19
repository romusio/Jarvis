package com.example.jarvis.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public final class GoogleCseParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private GoogleCseParser() {}

    public static List<InfoSearchService.SearchResult> parse(String json) {
        List<InfoSearchService.SearchResult> results = new ArrayList<>();
        try {
            JsonNode root = MAPPER.readTree(json);
            JsonNode items = root.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode it : items) {
                    String title = textOrNull(it.get("title"));
                    String link = textOrNull(it.get("link"));
                    String snippet = textOrNull(it.get("snippet"));
                    if (link != null) {
                        results.add(new InfoSearchService.SearchResult(title, link, snippet));
                    }
                }
            }
        } catch (Exception ignored) {}
        return results;
    }

    private static String textOrNull(JsonNode node) {
        return node == null ? null : node.asText();
    }
}





