package com.vttp.ssf.assessment.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

public class Book {
    private String key;
    private String title = "No Title";
    private String description = "No description";
    private List<String> excerpts = new LinkedList<String>();
    private boolean cached = false;

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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getExcerpts() {
        return this.excerpts;
    }

    public void setExcerpts(List<String> excerpts) {
        this.excerpts = excerpts;
    }

    public boolean isCached() {
        return this.cached;
    }

    public boolean getCached() {
        return this.cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public static Book createFromJsonObject(JsonObject object) {
        Book book = new Book();
        if (object.getString("key").length() > 8) {
            book.setKey(object.getString("key").substring(7));
        } else {
            book.setKey(object.getString("key"));
        }
        System.out.println("key: " + book.getKey());
        book.setTitle(object.getString("title"));
        System.out.println("title: " + book.getTitle());
        if (object.containsKey("description")) {
            System.out.println("description found");
            String description = object.getJsonObject("description")
                    .getString("value");
            book.setDescription(description);
        }
        System.out.println("description: " + book.getDescription());
        if (object.containsKey("excerpts")) {
            System.out.println("Contains excerpts");
            JsonArray excerptArray = object.getJsonArray("excerpts");
            if (excerptArray.size() > 0) {
                System.out.println("Excerpts retrieved");
                for (JsonValue jsonValue : excerptArray) {
                    String excerpt = jsonValue.asJsonObject()
                            .getString("excerpt");
                    System.out.println(excerpt);
                    book.excerpts.add(excerpt);
                }
            }
        } else {
            book.excerpts.add("No excerpts found.");
        }
        System.out.println("excerpts added");
        System.out.println("book succcessfully created");
        return book;
    }

    public String toJsonObjectString() {
        JsonArrayBuilder excerptsArrayBuilder = Json.createArrayBuilder();
        for (String entry : this.excerpts) {
            JsonObject excerptObject = Json.createObjectBuilder()
                    .add("excerpt", entry)
                    .build();
            excerptsArrayBuilder.add(excerptObject);
        }
        JsonObject description = Json.createObjectBuilder()
                .add("value", this.description)
                .build();
        JsonArray excerptsArray = excerptsArrayBuilder.build();
        JsonObject bookObject = Json.createObjectBuilder()
                .add("key", this.key)
                .add("title", this.title)
                .add("description", description)
                .add("excerpts", excerptsArray)
                .build();
        return bookObject.toString();
    }
}
