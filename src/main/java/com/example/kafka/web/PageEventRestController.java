package com.example.kafka.web;


import com.example.kafka.entities.PageEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageEventRestController {

    @GetMapping("/publish/{topic}/{name}")
    public PageEvent publish() {

    }

}
