package com.example.scrapingandai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.ai.vectorstore.SearchRequest;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatModel chatModel;
  private final VectorStore vectorStore;

  private String prompt = """
      Your task is to answer the questions about Republic of Srpska Constitution. Use the information from the DOCUMENTS
      section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
      simply state that you don't know the answer.
      
      QUESTION:
      {input}
      
      DOCUMENTS:
      {documents}
      
      """;

  @Override
  public String prompt(String message) {
    return chatModel.call(message);
  }

  @Override
  public String promptAboutLaws(String message) {

    PromptTemplate template
        = new PromptTemplate(prompt);
    Map<String, Object> promptsParameters = new HashMap<>();
    promptsParameters.put("input", message);
    promptsParameters.put("documents", findSimilarData(message));

    return chatModel
        .call(template.create(promptsParameters))
        .getResult()
        .getOutput().getText();
  }

  private String findSimilarData(String question) {

    List<Document> documents =
        vectorStore.similaritySearch(SearchRequest.builder()
            .query(question)
            .topK(5)
            .build());

    return documents
        .stream()
        .map(Document::getText)
        .collect(Collectors.joining());

  }
}
