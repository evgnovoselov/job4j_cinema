package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/films/one")
    public String filmOne() {
        return "films/one";
    }

    @GetMapping("/film-sessions")
    public String filmSessionList() {
        return "film-sessions/list";
    }
}
