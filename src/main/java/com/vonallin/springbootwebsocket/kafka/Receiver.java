package com.vonallin.springbootwebsocket.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class Receiver {
    @KafkaListener(topics = "test-topic2")
    public  void receiveMessage(String content){
        System.out.println(content);
    }

    @KafkaListener(topics = "test-topic2", containerFactory="kafkaContainer")
    public  void receiveFactory1Message(String content){
//        Message message=gson.fromJson(content,Message.class);
        System.out.println("receiveFactory1Message---->"+content);
    }
}
