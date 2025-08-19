package com.example.jarvis.voice;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jarvis.voice.provider", havingValue = "google")
public class GoogleTtsVoiceService implements VoiceService {

    private final String languageCode;

    public GoogleTtsVoiceService() {
        this.languageCode = System.getProperty("jarvis.voice.language", System.getenv().getOrDefault("JARVIS_VOICE_LANGUAGE", "ru-RU"));
    }

    @Override
    public byte[] synthesize(String text) {
        try {
            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder().build();
            try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings)) {
                SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
                VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                        .setLanguageCode(languageCode)
                        .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                        .build();
                AudioConfig audioConfig = AudioConfig.newBuilder()
                        .setAudioEncoding(AudioEncoding.MP3)
                        .build();
                return textToSpeechClient.synthesizeSpeech(input, voice, audioConfig).getAudioContent().toByteArray();
            }
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public String getContentType() {
        return "audio/mpeg";
    }
}


