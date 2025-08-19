package com.example.jarvis.voice;

public interface VoiceService {
    byte[] synthesize(String text);
    String getContentType();
}


