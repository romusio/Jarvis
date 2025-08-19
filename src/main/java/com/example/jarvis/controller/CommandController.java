package com.example.jarvis.controller;

import com.example.jarvis.core.JarvisCore;
import com.example.jarvis.model.CommandRequest;
import com.example.jarvis.model.CommandResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/command", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommandController {

    private final JarvisCore jarvisCore;

    public CommandController(JarvisCore jarvisCore) {
        this.jarvisCore = jarvisCore;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommandResponse handle(@Valid @RequestBody CommandRequest request) {
        return jarvisCore.processCommand(request.getText());
    }
}



