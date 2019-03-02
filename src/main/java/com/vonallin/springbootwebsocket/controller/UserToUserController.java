package com.vonallin.springbootwebsocket.controller;

import com.google.gson.Gson;
import com.vonallin.springbootwebsocket.dto.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserToUserController{
        @Autowired
        private SimpMessagingTemplate template;

        /**
         * 向执行用户发送请求
         */
        @RequestMapping(value = "send2user")
        @ResponseBody
        public int sendMq2User(HttpServletRequest request,String name){
            System.out.println( "=======" + name);
            RequestMessage demoMQ = new RequestMessage();
            demoMQ.setName(name);
            HttpServletRequest request2 = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            if(request2==request){
                System.out.println( "same");
            }else{
                System.out.println( "not same");
            }

            request.getSession().getAttribute("loginName");
            String result = new Gson().toJson(demoMQ);
            template.convertAndSendToUser(name, "/topic/demo", result);
            return 0;
        }
}
