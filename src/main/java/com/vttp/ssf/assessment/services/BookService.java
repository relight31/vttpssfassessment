package com.vttp.ssf.assessment.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vttp.ssf.assessment.model.Book;
import com.vttp.ssf.assessment.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class BookService {
    private static final String SEARCH_BASE_URL = "http://openlibrary.org/search.json";
    private static final String WORKS_BASE_URL = "https://openlibrary.org/works/%s.json";

    Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired
    BookRepository repository;

    public List<Book> search(String searchTerm) {
        // build URI and send to invokeMethod
        String searchBooksString = UriComponentsBuilder
                .fromUriString(SEARCH_BASE_URL)
                .queryParam("title", searchTerm)
                .toUriString();
        logger.log(Level.INFO, "searchBooksString constructed as " + searchBooksString);
        Optional<JsonObject> resultObject = invokeMethod(searchBooksString);
        logger.log(Level.INFO, "search results retrieved as JsonObject");
        List<Book> books = new LinkedList<>();
        if (resultObject.isPresent()) {
            logger.log(Level.INFO, "Optional unpacked");
            JsonArray booksArray = resultObject.get().getJsonArray("docs");
            for (JsonValue entry : booksArray) {
                if (books.size() >= 20) {
                    break;
                } else {
                    Book book = Book.createFromJsonObject(entry.asJsonObject());
                    books.add(book);
                }
            }
        }
        logger.log(Level.INFO, "Retrieved " + books.size() + " books");
        // max 20 books returned
        return books;
    }

    public Book getWork(String worksID) {
        // check cache first
        logger.log(Level.INFO, String.valueOf(repository.checkCache(worksID)));
        if (repository.checkCache(worksID) == true) {
            logger.log(Level.INFO, worksID + " available in cache");
            return repository.getBookFromCache(worksID);
        } else {
            // build URI and send to invokeMethod
            String searchWorksidString = UriComponentsBuilder
                    .fromUriString(WORKS_BASE_URL.formatted(worksID))
                    .toUriString();
            logger.log(Level.INFO, "searchWorksidString constructed");
            Optional<JsonObject> resultObject = invokeMethod(searchWorksidString);
            logger.log(Level.INFO, "work retrieved as JsonObject");
            Book book = new Book();
            if (resultObject.isPresent()) {
                JsonObject result = resultObject.get();
                book = Book.createFromJsonObject(result);
                logger.log(Level.INFO, "book of workID: " + book.getKey() + " created");
            }
            repository.storeBookInCache(book);
            return book;
        }
    }

    public static String cleanSearchQuery(String query) {
        return null;
    }

    public static Optional<JsonObject> invokeMethod(String url) {
        RequestEntity<Void> req = RequestEntity.get(url)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        try (InputStream is = new ByteArrayInputStream(resp.getBody().getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject object = reader.readObject();
            return Optional.of(object);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
