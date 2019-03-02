package com.vonallin.springbootwebsocket.intercept;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    // 收到消息记数
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpSession httpSession = getSession(request);
        if (request.getURI().getPath().endsWith("websocket")&&httpSession.getAttribute("loginName") == null) {
            String loginName = count.incrementAndGet() + "";
            System.out.println("loginName::::::"+loginName);
            httpSession.setAttribute("loginName", loginName);
        }


//  String user = (String)httpSession.getAttribute("loginName");
//
//        if(StringUtils.isEmpty(user)){
//            System.out.println("未登录系统，禁止登录websocket!");
//            return false;
//        }
//        System.out.println("login = " + user);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    // 参考 HttpSessionHandshakeInterceptor
    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(true);
        }
        return null;
    }
}
