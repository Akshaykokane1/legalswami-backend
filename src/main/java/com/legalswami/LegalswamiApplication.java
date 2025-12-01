package com.legalswami;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.legalswami"})
public class LegalswamiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegalswamiApplication.class, args);
    }
}
