package com.example.scrapingandai.controller;

import com.example.scrapingandai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

  private final ChatService service;

  @GetMapping
  public String prompt(@RequestParam String message) {
    return service.prompt(message);
  }

  @GetMapping("/laws")
  public String promptAboutLaws(@RequestParam String message) {
    return service.promptAboutLaws(message);
  }
}
