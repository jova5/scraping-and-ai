package com.example.scrapingandai.service;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataLoaderServiceImpl implements DataLoaderService {

  private final VectorStore vectorStore;
  private final JdbcClient jdbcClient;
  private final ResourceLoader resourceLoader;
  private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

  @Override
  public void loadTextData() {

    try {

      Integer count = jdbcClient.sql("select count(*) from vector_store")
          .query(Integer.class)
          .single();

      System.out.println("Number of records in vector stores: " + count);

      if (count == 0) {

        System.out.println("Processing new data");

        Resource[] resources = resolver.getResources("classpath*:" + "html_laws" + "/**");

        for (Resource resource : resources) {
          if (resource.exists() && resource.isReadable()
              && resource.contentLength() > 0) { // Check if it's a file, not a directory

            System.out.println("Processing file: " + resource.getFilename());
            loadDataFromTxt(resource);
          }
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void loadDataFromTxt(Resource resource) {

    var textReader = new TextReader(resource); // No config needed here
    var documents = textReader.get();

    var textSplitter = new TokenTextSplitter();
    var splitDocuments = textSplitter.apply(documents);

    vectorStore.accept(splitDocuments);

    System.out.println("Successfully processed file: " + resource.getFilename());
  }

//  private void loadDataFromPdf() {
//    PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
//        .withPagesPerDocument(1)
//        .build();
//
//    PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);
//
//    var textSplitter = new TokenTextSplitter();
//    vectorStore.accept(textSplitter.apply(reader.get()));
//  }

}
