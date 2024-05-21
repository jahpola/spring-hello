package org.kerminator.hello.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloController {

    //Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping(path = "/")
    public String hello() {
        return "Hello World";
    }
    
    @GetMapping(path="/test")
    public String test() {
        return "Test Test";
    }
}