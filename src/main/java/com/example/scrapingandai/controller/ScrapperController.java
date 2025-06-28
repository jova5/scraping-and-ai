package com.example.scrapingandai.controller;

import com.example.scrapingandai.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scrapper")
public class ScrapperController {

  private final ScrapperService scrapperService;

  @PostMapping("/pdf")
  public void startScrapping() {
    scrapperService.startScrappingPdf();
  }
}

