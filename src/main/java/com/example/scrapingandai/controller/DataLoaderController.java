package com.example.scrapingandai.controller;

import com.example.scrapingandai.service.DataLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/data-loader")
public class DataLoaderController {

  private final DataLoaderService service;

  @PostMapping
  public void loadTextData() {
    service.loadTextData();
  }

}
