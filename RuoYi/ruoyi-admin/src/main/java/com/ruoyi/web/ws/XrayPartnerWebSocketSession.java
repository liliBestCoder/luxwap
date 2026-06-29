package com.ruoyi.web.ws;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class XrayPartnerWebSocketSession implements WebSocketSession {
    private WebSocketSession session;
    private long lastReadTime = System.currentTimeMillis();

    public XrayPartnerWebSocketSession(WebSocketSession session){
        this.session = session;
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public URI getUri() {
        return session.getUri();
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return session.getHandshakeHeaders();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return session.getAttributes();
    }

    @Override
    public Principal getPrincipal() {
        return session.getPrincipal();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return session.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return session.getRemoteAddress();
    }

    @Override
    public String getAcceptedProtocol() {
        return session.getAcceptedProtocol();
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
        session.setTextMessageSizeLimit(messageSizeLimit);
    }

    @Override
    public int getTextMessageSizeLimit() {
        return session.getTextMessageSizeLimit();
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
        session.setBinaryMessageSizeLimit(messageSizeLimit);
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return session.getBinaryMessageSizeLimit();
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return session.getExtensions();
    }

    @Override
    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        session.sendMessage( message);
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public void close() throws IOException {
        session.close();
    }

    @Override
    public void close(CloseStatus status) throws IOException {
        session.close(status);
    }

    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() - lastReadTime) <= 6 * 10 * 1000;
    }
}

