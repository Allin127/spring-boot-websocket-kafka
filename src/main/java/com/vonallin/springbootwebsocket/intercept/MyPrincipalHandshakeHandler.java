package com.vonallin.springbootwebsocket.intercept;

import com.vonallin.springbootwebsocket.dto.MyPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyPrincipalHandshakeHandler  extends DefaultHandshakeHandler {
    
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpSession httpSession = getSession(request);
        String user = (String)httpSession.getAttribute("loginName");

        if(StringUtils.isEmpty(user)){
            System.out.println("未登录系统，禁止登录websocket!");
            return null;
        }
        System.out.println(" MyDefaultHandshakeHandler login = " + user);
        return new MyPrincipal(user);
    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
