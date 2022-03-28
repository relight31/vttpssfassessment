package com.vttp.ssf.assessment.controllers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vttp.ssf.assessment.model.Book;
import com.vttp.ssf.assessment.services.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class SearchController {
    Logger logger = Logger.getLogger(SearchController.class.getName());

    @Autowired
    BookService service;

    @GetMapping(path = "/search")
    public String search(Model model, @RequestParam(name = "booktitle") String searchTerm) {
        List<Book> books = service.search(searchTerm);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("books", books);
        return "search";
    }

    @GetMapping(path = "/index")
    public String index() {
        logger.log(Level.INFO, "returning to index page");
        return "index";
    }
}