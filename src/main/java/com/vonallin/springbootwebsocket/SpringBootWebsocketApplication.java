package com.vonallin.springbootwebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SpringBootWebsocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebsocketApplication.class, args);
    }
}

