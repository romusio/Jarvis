package com.example.jarvis.voice;

import org.springframework.stereotype.Service;

@Service
public class NoopVoiceService implements VoiceService {
    @Override
    public byte[] synthesize(String text) {
        // Возвращаем пустой WAV со словом "disabled"
        return new byte[0];
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }
}





