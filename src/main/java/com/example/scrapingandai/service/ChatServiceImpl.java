package com.example.scrapingandai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatModel chatModel;

  @Override
  public String prompt(String message) {
    return chatModel.call(message);
  }
}
