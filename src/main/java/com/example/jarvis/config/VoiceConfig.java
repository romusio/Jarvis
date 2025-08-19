package com.example.jarvis.config;

import com.example.jarvis.voice.GoogleTtsVoiceService;
import com.example.jarvis.voice.NoopVoiceService;
import com.example.jarvis.voice.VoiceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoiceConfig {

    @Bean
    @ConditionalOnProperty(name = "jarvis.voice.provider", havingValue = "google")
    public VoiceService googleVoiceService() {
        return new GoogleTtsVoiceService();
    }

    @Bean
    @ConditionalOnMissingBean(VoiceService.class)
    public VoiceService noopVoiceService() {
        return new NoopVoiceService();
    }
}





