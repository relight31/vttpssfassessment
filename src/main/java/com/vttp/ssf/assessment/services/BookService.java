package com.vttp.ssf.assessment.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.vttp.ssf.assessment.model.Book;

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

    Logger logger = Logger.getLogger(BookService.class.getName());

    public List<Book> search(String searchTerm) {
        // build URI and send to invokeMethod
        String searchBooksString = UriComponentsBuilder
                .fromUriString(SEARCH_BASE_URL)
                .queryParam("q", searchTerm)
                .toUriString();
        Optional<JsonObject> resultObject = invokeMethod(searchBooksString);
        List<Book> books = new LinkedList<>();
        if (resultObject.isPresent()) {
            JsonArray booksArray = resultObject.get().getJsonArray("docs");
            for (JsonValue entry : booksArray) {
                Book book = new Book();
                book.setKey(entry.asJsonObject().getString("key"));
                book.setTitle(entry.asJsonObject().getString("title"));
                books.add(book);
            }
        }
        // max 20 books returned
        return books;
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
