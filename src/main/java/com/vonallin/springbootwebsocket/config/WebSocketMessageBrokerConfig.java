package com.vonallin.springbootwebsocket.config;

import com.vonallin.springbootwebsocket.intercept.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
// 此注解表示使用STOMP协议来传输基于消息代理的消息，此时可以在@Controller类中使用@MessageMapping
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig extends WebSocketMessageBrokerConfigurationSupport implements WebSocketMessageBrokerConfigurer {
    //endpoint的拦截器
    @Autowired
    private MyHandshakeInterceptor myHandShakeInterceptor;
    //消息生命周期的监听，
    @Autowired
    private MyChannelInterceptorAdapter myChannelInterceptorAdapter;
    @Autowired
    private MyPrincipalHandshakeHandler myDefaultHandshakeHandler;
    @Autowired
    private AuthHandshakeInterceptor sessionAuthHandshakeInterceptor;
    @Autowired
    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
    public int HEART_BEAT = 5000;

    @PostConstruct
    public void init() {
        webSocketMessageBrokerStats.setLoggingPeriod(10 * 1000); // desired time in millis
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * 注册 Stomp的端点
         *
         * addEndpoint：添加STOMP协议的端点。这个HTTP URL是供WebSocket或SockJS客户端访问的地址
         * withSockJS：指定端点使用SockJS协议
          */
        registry.addEndpoint("/websocket-simple")
                .setAllowedOrigins("*") // 添加允许跨域访问
                .addInterceptors(myHandShakeInterceptor) // 添加自定义拦截
                .withSockJS().setClientLibraryUrl("https://cdn.jsdelivr.net/sockjs/1/sockjs.min.js");

        registry.addEndpoint("/websocket-simple2")
                .setAllowedOrigins("*") // 添加允许跨域访问
                .addInterceptors(myHandShakeInterceptor) // 添加自定义拦截
                .withSockJS().setClientLibraryUrl("https://cdn.jsdelivr.net/sockjs/1/sockjs.min.js");

        registry.addEndpoint("/websocket-simple-single")
                .setAllowedOrigins("*") // 添加允许跨域访问
                .addInterceptors(sessionAuthHandshakeInterceptor)
                .setHandshakeHandler(myDefaultHandshakeHandler)
                .withSockJS().setClientLibraryUrl("https://cdn.jsdelivr.net/sockjs/1/sockjs.min.js");


    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {


        /**
         * 配置消息代理
         * 启动简单Broker，消息的发送的地址符合配置的前缀来的消息才发送到这个broker
         *  Enable a simple message broker and configure one or more prefixes to filte destinations targeting the broker
         */
        registry.enableSimpleBroker("/topic","/queue");


        /**
         * 配置心跳，默认不会有心跳去保持链接的
         */
        //设置简单的消息代理器，它使用Memory（内存）作为消息代理器，
        //其中/user和/topic都是我们发送到前台的数据前缀。前端必须订阅以/user开始的消息（.subscribe()进行监听）。
        //setHeartbeatValue设置后台向前台发送的心跳，
        //注意：setHeartbeatValue这个不能单独设置，不然不起作用，要配合后面setTaskScheduler才可以生效。
        //对应的解决方法的网址：https://stackoverflow.com/questions/39220647/spring-stomp-over-websockets-not-scheduling-heartbeats
        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
        te.setPoolSize(1);
        te.setThreadNamePrefix("wss-heartbeat-thread-");
        te.initialize();
        registry.enableSimpleBroker("/user","/topic")
                .setHeartbeatValue(new long[]{HEART_BEAT,HEART_BEAT})
                .setTaskScheduler(te);

        //点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        //registry.setUserDestinationPrefix("/user/");
        // 置客户端发送信息的路径的前缀
        //设置我们前端发送：websocket请求的前缀地址。即client.send("/app")作为前缀，然后再加上对应的@MessageMapping后面的地址
        // registry.setApplicationDestinationPrefixes("/app");

//        registry.setPathMatcher(new AntPathMatcher("."));    //可以已“.”来分割路径，看看类级别的@messageMapping和方法级别的@messageMapping
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        super.configureClientInboundChannel(registration);
        registration.interceptors(myChannelInterceptorAdapter);
    }



    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        super.configureWebSocketTransport(registry);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return super.configureMessageConverters(messageConverters);
    }


    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        super.configureClientOutboundChannel(registration);
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        super.addReturnValueHandlers(returnValueHandlers);
    }
    @Override
    @Bean
    public WebSocketHandler subProtocolWebSocketHandler() {
        return new CustomSubProtocolWebSocketHandler(clientInboundChannel(), clientOutboundChannel());
    }

//    不使用注入，直接覆盖定义Bean的方法
//    @Override
//    @Bean
//    public WebSocketMessageBrokerStats webSocketMessageBrokerStats() {
//        WebSocketMessageBrokerStats stats = super.webSocketMessageBrokerStats();
//        stats.setLoggingPeriod(10000);
//        return stats;
//    }

}
