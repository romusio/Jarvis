package com.example.jarvis.controller;

import com.example.jarvis.voice.VoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/voice")
public class VoiceController {

    private final VoiceService voiceService;

    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @PostMapping(path = "/speak", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> speak(@RequestBody String text) {
        byte[] data = voiceService.synthesize(text);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, voiceService.getContentType())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(data);
    }
}





