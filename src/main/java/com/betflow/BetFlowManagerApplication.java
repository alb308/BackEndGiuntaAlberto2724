package com.betflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BetFlowManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BetFlowManagerApplication.class, args);
    }
}
