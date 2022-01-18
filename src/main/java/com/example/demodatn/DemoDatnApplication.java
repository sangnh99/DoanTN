package com.example.demodatn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoDatnApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDatnApplication.class, args);
    }

}
