package com.ruoyi.web.ws.config;

import com.ruoyi.web.ws.handler.XrayPartnerWebSocketHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ApplicationContext applicationContext;

    public WebSocketConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(xrayPartnerWebSocketHandler(), "/ws")
                .setAllowedOrigins("*"); // 若有前端域名，写具体地址
    }

    @Bean
    public XrayPartnerWebSocketHandler xrayPartnerWebSocketHandler() {
        return new XrayPartnerWebSocketHandler(applicationContext);
    }
}

