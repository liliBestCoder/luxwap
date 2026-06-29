package com.ruoyi.web.ws.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.mapper.XrayUserMapper;
import com.ruoyi.system.event.MsgEvent;
import com.ruoyi.web.ws.XrayPartnerWebSocketSession;
import com.ruoyi.web.ws.service.MsgService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class XrayPartnerWebSocketHandler extends TextWebSocketHandler implements ApplicationListener<MsgEvent> {
    private static final Logger log = LoggerFactory.getLogger(XrayPartnerWebSocketHandler.class);

    private Map<String, XrayPartnerWebSocketSession> sessionMap = new HashMap<>();
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    private ApplicationContext applicationContext;

    public XrayPartnerWebSocketHandler(ApplicationContext applicationContext){

        this.applicationContext = applicationContext;

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            synchronized (this){
                for (Map.Entry<String, XrayPartnerWebSocketSession> entry : sessionMap.entrySet()) {
                    XrayPartnerWebSocketSession session = entry.getValue();
                    String clientIp = entry.getKey();
                    //已经失活的 或者 心跳检测超时的session 需要被手动移除 如果需要 需要手动关闭 释放资源
                    if (!session.isOpen() || !session.isAlive()) {
                        sessionMap.remove(clientIp);
                        if(session.isOpen()){
                            try {
                                session.close();
                            }catch (Exception e){
                                log.error("XrayPartnerWebSocketHandler close session error, clientIp : {}", clientIp, e);
                            }
                        }
                    }else{
                        try{
                            JSONObject pingMsg = new JSONObject();
                            pingMsg.put("type", "ping");
                            session.sendMessage(new TextMessage(pingMsg.toJSONString()));
                        }catch (Exception e){
                            log.error("XrayPartnerWebSocketHandler send ping error, clientIp : {}", clientIp, e);
                        }
                    }
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            applicationContext.publishEvent(new MsgEvent(this, "sync_users", null, null));
        }, 1, 10, TimeUnit.MINUTES);
    }

    @Autowired
    private MsgService msgService;
    @Autowired
    private XrayUserMapper xrayUserMapper;

    public static class UserData{
        private String user_id;
        private String email;
        private String op;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public static UserData from(XrayUser user){
            UserData userData = new UserData();
            userData.setUser_id(user.getUuid());
            userData.setEmail(user.getUniqueEmail());
            userData.setOp(user.getExpiration().before(new Date()) ? "remove" : "add");
            return userData;
        }

        public static UserData fromWithOutOp(XrayUser user){
            UserData userData = new UserData();
            userData.setUser_id(user.getUuid());
            userData.setEmail(user.getUniqueEmail());
            return userData;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientIp = session.getRemoteAddress().getAddress().getHostAddress();
        log.debug("XrayPartnerWebSocketHandler connected clientIp : {}", clientIp);
        synchronized (this){
            sessionMap.put(clientIp, new XrayPartnerWebSocketSession(session));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        String clientIp = session.getRemoteAddress().getAddress().getHostAddress();
        log.debug("XrayPartnerWebSocketHandler receive msg : {}, clientId : {}", msg, clientIp);
        synchronized (this){
            XrayPartnerWebSocketSession xrayPartnerWebSocketSession = sessionMap.get(clientIp);
            if(xrayPartnerWebSocketSession != null){
                xrayPartnerWebSocketSession.updateLastReadTime();
                JSONObject msgObj = JSON.parseObject(msg);
                String type = msgObj.getString("type");
                applicationContext.publishEvent(new MsgEvent(this, type, clientIp, msgObj));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String clientIp = session.getRemoteAddress().getAddress().getHostAddress();
        log.debug("XrayPartnerWebSocketHandler connection closed, clientId : {}", clientIp);
        synchronized (this){
            sessionMap.remove(clientIp);
        }
    }

    @Override
    public void onApplicationEvent(MsgEvent event) {
        String clientIp = event.getClientIp();
        String type = event.getType();
        switch (type) {
            case "traffic_collect":
                msgService.saveTrafficCollectList(clientIp, (JSONObject) event.getMsg());
                break;
            case "xray_status":
                msgService.updateXrayStatus(clientIp, (JSONObject) event.getMsg());
                break;
            case "sync_users":
                List<XrayUser> xrayUserList = xrayUserMapper.selectXrayUserList(new XrayUser());
                List<UserData> userDataList = xrayUserList.stream().map(UserData::from).collect(Collectors.toList());
                JSONObject syncUsersMsg = new JSONObject();
                syncUsersMsg.put("type", "sync_users");
                syncUsersMsg.put("data", userDataList);

                Map<String, XrayPartnerWebSocketSession> sessionMapCopy;
                if(StringUtils.isNotBlank(clientIp)){
                    sessionMapCopy = new HashMap<>(1);
                    sessionMapCopy.put(clientIp, sessionMap.get(clientIp));

                }else {
                    sessionMapCopy = dumpSessions();
                }

                broadcast(syncUsersMsg.toJSONString(), sessionMapCopy);
                break;
            case "add_user":
                XrayUser xrayUser = (XrayUser)event.getMsg();
                JSONObject addUserMsg = new JSONObject();
                addUserMsg.put("type", "add_user");
                addUserMsg.put("data", UserData.fromWithOutOp(xrayUser));
                broadcast(addUserMsg.toJSONString());
                break;
            case "remove_user":
                XrayUser user = (XrayUser)event.getMsg();
                JSONObject removeUserMsg = new JSONObject();
                removeUserMsg.put("type", "remove_user");
                removeUserMsg.put("data", UserData.fromWithOutOp(user));
                broadcast(removeUserMsg.toJSONString());
                break;
        }
    }

    private void broadcast(String msgStr) {
        Map<String, XrayPartnerWebSocketSession> sessionMap = dumpSessions();
        broadcast(msgStr, sessionMap);
    }

    private void broadcast(String msgStr, Map<String, XrayPartnerWebSocketSession> sessionMap) {
        for (Map.Entry<String, XrayPartnerWebSocketSession> entry : sessionMap.entrySet()) {
            String clientIpReal = entry.getKey();
            XrayPartnerWebSocketSession session = entry.getValue();
            try{
                session.sendMessage(new TextMessage(msgStr));
            }catch (Exception e){
                log.error("XrayPartnerWebSocketHandler broadcast error, clientIp : {}", clientIpReal, e);
            }
        }
    }

    private Map<String, XrayPartnerWebSocketSession> dumpSessions() {
        Map<String, XrayPartnerWebSocketSession> sessionCopy;
        synchronized (this) {
            sessionCopy = new HashMap<>(sessionMap);
        }
        return sessionCopy;
    }
}

