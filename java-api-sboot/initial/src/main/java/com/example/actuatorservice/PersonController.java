package com.example.actuatorservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

@Controller
public class PersonController {
    private final AtomicLong counter = new AtomicLong();
    @GetMapping("/person")
    @ResponseBody
    public Person getPerson(@RequestParam(name="fullName", required=false, defaultValue="David") String fullName,
                            @RequestParam(name="birthDay", required=false,defaultValue = "1/1/2020") String birthDay
                            ){
        return new Person(counter.incrementAndGet(),fullName,birthDay);
    }
}
