package com.vttp.ssf.assessment.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/book")
public class BookController {
    @GetMapping(path = "/{works_id}")
    public String getBook(@PathVariable(name = "works_id") String worksID, Model model) {
        return null;
    }
}
