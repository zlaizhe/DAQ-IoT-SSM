package com.my.iot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class HomeController {
    private final String[] s = {
            "Talk is cheap. Show me the code.",
            "Stay hungry Stay foolish.",
            "Go big or go home.",
            "Done is better than perfect."
    };
    private final Random r = new Random();

    @RequestMapping("/home")
    public String home() {
        return s[r.nextInt(4)];
    }
}
