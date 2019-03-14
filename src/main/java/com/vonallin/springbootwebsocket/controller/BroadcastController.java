package com.vonallin.springbootwebsocket.controller;

import com.google.gson.Gson;
import com.vonallin.springbootwebsocket.dto.ResponseMessage;
import com.vonallin.springbootwebsocket.kafka.Receiver;
import com.vonallin.springbootwebsocket.kafka.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class BroadcastController {
    private static final Logger logger = LoggerFactory.getLogger(BroadcastController.class);

    // 收到消息记数
    private AtomicInteger count = new AtomicInteger(0);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receive;

    /**
     * @MessageMapping 指定要接收消息的地址，类似@RequestMapping。除了注解到方法上，也可以注解到类上
     * @SendTo默认 消息将被发送到与传入消息相同的目的地
     * 消息的返回值是通过{@link org.springframework.messaging.converter.MessageConverter}进行转换
     * @param requestMessage
     * @return
     */
//    @MessageMapping("/receive")
////    @SendToUser("/topic/getResponse")
//    @SendTo("/topic/getResponse")
//    public ResponseMessage broadcast(RequestMessage requestMessage){
////        logger.info("receive message = {}" , JSONObject.toJSONString(requestMessage));
//        ResponseMessage responseMessage = new ResponseMessage();
//        responseMessage.setResponseMessage("BroadcastController receive [" + count.incrementAndGet() + "] records");
//        return responseMessage;
//    }

    @MessageMapping("/receive")
//    @SendToUser("/topic/getResponse")
    @SendTo("/topic/getResponse")
    public ResponseMessage broadcast(String requestMessage){
        logger.info("receive message = {}" , requestMessage);
        Gson gson = new Gson();
        Map map =  gson.fromJson(requestMessage,Map.class);
       logger.info("receive message = {}" , map);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMessage("BroadcastController receive [" + count.incrementAndGet() + "] records");

        sender.sendMessage();

        return responseMessage;
    }
    @MessageMapping("/sendToUser")
    @SendToUser("/topic/getResponse")
    public ResponseMessage sendUserResponse(String requestMessage, Principal principal){
        logger.info("receive message = {}" , requestMessage);
        principal.getName();
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMessage("BroadcastController receive [" + count.incrementAndGet() + "] records");
        return responseMessage;
    }


    @MessageMapping("/login")
//    @SendToUser("/topic/getResponse")
    @SendTo("/topic/loginResult")
    public ResponseMessage broadcastLogin(String requestMessage){
        logger.info("receive broadcastLogin message = {}" , requestMessage);
        Gson gson = new Gson();
        Map map =  gson.fromJson(requestMessage,Map.class);
        logger.info("receive  broadcastLogin message = {}" , map);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMessage("LoginSuccess");
        return responseMessage;
    }


    @RequestMapping(value="/broadcast/index")
    public String broadcastIndex(HttpServletRequest req){
        System.out.println(req.getRemoteHost());
        return "websocket/simple/ws-broadcast";
    }

    @RequestMapping(value="/test",method = RequestMethod.GET)
    @ResponseBody
    public String broadcastTest(HttpServletRequest req){
        messagingTemplate.convertAndSend("/topic/getResponse","hello,client");
//        messagingTemplate.sendMessage("hihi");
        System.out.println(simpUserRegistry.getUserCount());
        return "helloworld";
    }


    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListner(SessionConnectEvent event) {
        logger.info("Received a new web socket connection : "  + new Date());
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListner(SessionDisconnectEvent event) {
        logger.info("session closed : " + new Date());
    }
}
