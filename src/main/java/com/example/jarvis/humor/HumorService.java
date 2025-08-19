package com.example.jarvis.humor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class HumorService {
    private final Random random = new Random();
    private final List<String> suffixes = List.of(
            " Что-нибудь ещё?",
            " Если это спасёт мир — я за.",
            " Я шучу, но напоминание настоящее.",
            " Да, хозяин. Почти как в кино.",
            " Готово. И да, я блестяще справился."
    );

    public String decorate(String plainReply) {
        String suffix = suffixes.get(random.nextInt(suffixes.size()));
        return plainReply.endsWith(".") || plainReply.endsWith("!") ? (plainReply + " " + suffix) : (plainReply + ". " + suffix);
    }
}


