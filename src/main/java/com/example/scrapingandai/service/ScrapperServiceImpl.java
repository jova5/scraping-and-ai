package com.example.scrapingandai.service;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapperServiceImpl implements ScrapperService {

  @Override
  public void startScrappingPdf() {

    var url = "https://www.rgurs.org/uploads/laws/";

    try {

      log.info("Scrapping URL: {}", url);

      var document = Jsoup.connect(url).get();
      var elements = document.select("tbody > tr");

      for (int i = 3; i < elements.size(); i++) {

        var fileName = elements.get(i).select("td > a").text();
        var pdfUrl = elements.get(i).select("td > a").attr("abs:href");

        if (fileName.isBlank() || pdfUrl.isBlank() || !fileName.contains(".pdf")) {
          continue;
        }

        log.info("Downloading file: {}", fileName);
        var readableByteChannel = Channels.newChannel(
            new URI(pdfUrl).toURL().openStream());
        var outputPath = Paths.get("pdf_laws", fileName);
        Files.createDirectories(outputPath.getParent());
        var fileOutputStream = new FileOutputStream(outputPath.toFile());
        var fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        fileOutputStream.close();
        log.info("File downloaded: {}", fileName);
      }

      log.info("===== FINISHED =====");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
