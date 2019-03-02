package com.vonallin.springbootwebsocket.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Sender {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(){
        kafkaTemplate.send("test-topic2","hello world");
    }
}
