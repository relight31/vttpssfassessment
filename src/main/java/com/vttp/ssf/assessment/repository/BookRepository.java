package com.vttp.ssf.assessment.repository;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vttp.ssf.assessment.model.Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

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
        Book book = (Book) redisTemplate.opsForHash()
                .get(worksID, "entry");
        logger.log(Level.INFO, "Successfully retrieved book: " + book.getTitle() + " with works ID: " + book.getKey());
        return book;
    }

    public void storeBookInCache(Book book) {
        logger.log(Level.INFO, "Storing book " + book.getKey() + " in cache");
        redisTemplate.opsForHash()
                .put(book.getKey(), "entry", book);

        Duration timeout = Duration.parse("10M");
        redisTemplate.expire(book.getKey(), timeout);
        System.out.println(timeout.toMinutesPart());
        logger.log(Level.INFO, "Successfully cached book: " + book.getTitle() + " at key: " + book.getKey() + " for "
                + timeout.toMinutesPart() + " minutes");
    }
}
