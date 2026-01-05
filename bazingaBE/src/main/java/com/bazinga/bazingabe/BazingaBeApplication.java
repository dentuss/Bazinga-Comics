package com.bazinga.bazingabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BazingaBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BazingaBeApplication.class, args);
    }
}
