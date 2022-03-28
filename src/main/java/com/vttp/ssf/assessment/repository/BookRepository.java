package com.vttp.ssf.assessment.repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vttp.ssf.assessment.model.Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Repository
public class BookRepository {
    // for caching in redis
    // redis allows expiration of entire key but not individual hashkey/value pairs

    Logger logger = Logger.getLogger(BookRepository.class.getName());

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public boolean checkCache(String worksID) {
        return redisTemplate.hasKey(worksID);
    }

    public Book getBookFromCache(String worksID) {
        String bookString = (String) redisTemplate.opsForHash()
                .get(worksID, "entry");
        logger.log(Level.INFO, bookString);
        try (InputStream is = new ByteArrayInputStream(bookString.getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject object = reader.readObject();
            logger.log(Level.INFO, "created JSON object of book");
            Book book = Book.createFromJsonObject(object);
            book.setCached(true);
            return book;
        } catch (Exception e) {
            logger.log(Level.INFO, "failed to retrieve book from cache");
            return null;
        }
    }

    public void storeBookInCache(Book book) {
        logger.log(Level.INFO, "Storing book " + book.getKey() + " in cache");
        redisTemplate.opsForHash()
                .put(book.getKey(), "entry", book.toJsonObjectString());

        Duration timeout = Duration.parse("PT10M");
        redisTemplate.expire(book.getKey(), timeout);
        System.out.println(timeout.toMinutesPart());
        logger.log(Level.INFO, "Successfully cached book: " + book.getTitle() + " at key: " + book.getKey() + " for "
                + timeout.toMinutesPart() + " minutes");
    }
}
