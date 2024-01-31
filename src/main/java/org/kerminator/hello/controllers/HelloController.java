package org.kerminator.hello.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    Logger logger = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping("/")
    public String hello() {
        return "Hello World";
    }
    
    @RequestMapping("/test")
    public String test() {
        return "Test Test";
    }
}