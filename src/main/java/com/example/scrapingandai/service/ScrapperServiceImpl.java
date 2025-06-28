package com.example.scrapingandai.service;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

  private static final String ILLEGAL_CHARS = "[<>:\"/\\|?*]";

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

  @Override
  public void startScrappingHtml() {

    var url = "https://www.paragraf.ba/besplatni-propisi-republike-srpske.html";

    try {

      var document = Jsoup.connect(url).get();
      var elements = document.select("ul");

      for (var element : elements) {

        if (!element.id().equals("propisi-la")) {
          continue;
        }

        var links = element.select("li > a");

        for (var link : links) {
          var linkName = link.text();
          var indexOfParenthesis = linkName.indexOf("(");

          String fileName;
          if (indexOfParenthesis != -1) {
            fileName = linkName.substring(0, indexOfParenthesis).trim() + ".txt";
          } else {
            fileName = linkName.trim() + ".txt";
          }

          log.info("Processing: {}", fileName);

          var newDocument = Jsoup.connect(link.attr("abs:href")).get();
          var article = newDocument.select("article").first();
          var articleHtml = article.outerHtml();
          var htmlSaveDir = "html_laws";
          var saveDirPath = Paths.get(htmlSaveDir);
          Files.createDirectories(saveDirPath);

          var filePath = saveDirPath.resolve(fileName.replaceAll(ILLEGAL_CHARS, "_"));
          try (var writer = new FileWriter(filePath.toFile())) {
            var defaultText = ArticleExtractor.INSTANCE.getText(articleHtml);
            writer.write(defaultText);
          }
          log.info("Successfully processed: {}", fileName);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
