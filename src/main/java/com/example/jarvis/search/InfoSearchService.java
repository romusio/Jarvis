package com.example.jarvis.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class InfoSearchService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String googleApiKey;
    private final String googleCseId;

    public InfoSearchService(
            @Value("${GOOGLE_API_KEY:}") String googleApiKey,
            @Value("${JARVIS_GOOGLE_CSE_ID:}") String googleCseId
    ) {
        this.googleApiKey = googleApiKey;
        this.googleCseId = googleCseId;
    }

    public List<SearchResult> searchWeb(String query, int limit) {
        if (googleApiKey == null || googleApiKey.isBlank() || googleCseId == null || googleCseId.isBlank()) {
            return List.of();
        }
        try {
            String url = "https://www.googleapis.com/customsearch/v1?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&num=" + Math.min(Math.max(limit, 1), 10)
                    + "&key=" + URLEncoder.encode(googleApiKey, StandardCharsets.UTF_8)
                    + "&cx=" + URLEncoder.encode(googleCseId, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return GoogleCseParser.parse(resp.body());
        } catch (Exception e) {
            return List.of();
        }
    }

    public PagePreview fetchPreview(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Jarvis)").timeout(8000).get();
            String title = doc.title();
            String text = doc.select("meta[name=description]").attr("content");
            if (text == null || text.isBlank()) {
                text = doc.select("p").stream().limit(3).map(el -> el.text()).reduce("", (a, b) -> a + " " + b);
            }
            return new PagePreview(title, url, text);
        } catch (IOException e) {
            return new PagePreview(null, url, null);
        }
    }

    public record SearchResult(String title, String link, String snippet) {}
    public record PagePreview(String title, String url, String summary) {}
}





