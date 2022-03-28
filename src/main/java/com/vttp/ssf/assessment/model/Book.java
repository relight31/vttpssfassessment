package com.vttp.ssf.assessment.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Book {
    private String key;
    private String title;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Book createBook(String jsonString) {
        Book book = new Book();
        try (InputStream is = new ByteArrayInputStream(jsonString.getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject object = reader.readObject();
            book.setKey(object.getString("key"));
            book.setTitle(object.getString("title"));
        } catch (IOException ex) {
            // assume no error
        }
        return book;
    }
}
