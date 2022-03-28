package com.vttp.ssf.assessment.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vttp.ssf.assessment.model.Book;
import com.vttp.ssf.assessment.services.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/book")
public class BookController {
    Logger logger = Logger.getLogger(BookController.class.getName());
    @Autowired
    BookService service;

    @GetMapping(path = "/{works_id}")
    public String getBook(@PathVariable(name = "works_id") String worksID, Model model) {
        logger.log(Level.INFO, "calling getBook method on worksID: " + worksID);
        Book book = service.getWork(worksID);
        model.addAttribute("book", book);
        return "book";
    }
}
