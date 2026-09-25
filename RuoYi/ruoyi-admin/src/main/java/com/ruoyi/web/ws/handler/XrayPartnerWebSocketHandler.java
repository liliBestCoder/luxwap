package com.ruoyi.web.ws.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.domain.VpnLines;
import com.ruoyi.system.domain.XrayNodeCommand;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.mapper.VpnLinesMapper;
import com.ruoyi.system.mapper.XrayNodeCommandMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class XrayPartnerWebSocketHandler extends TextWebSocketHandler implements ApplicationListener<MsgEvent> {
    private static final Logger log = LoggerFactory.getLogger(XrayPartnerWebSocketHandler.class);

    private Map<String, XrayPartnerWebSocketSession> sessionMap = new HashMap<>();
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    /** 内存级 sync_users 分片滑动窗口维护表: clientIp -> SyncBatch */
    private final Map<String, SyncBatch> activeSyncBatches = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    public static class SyncBatch {
        private final String batchId;
        private final String clientIp;
        private final int totalSeq;
        private final Map<Integer, String> chunks;
        private final Set<Integer> ackedSeqs = ConcurrentHashMap.newKeySet();
        private final AtomicInteger minAckSeq = new AtomicInteger(0);
        private volatile long lastSendTime;
        private int retryCount = 0;

        public SyncBatch(String batchId, String clientIp, int totalSeq, Map<Integer, String> chunks) {
            this.batchId = batchId;
            this.clientIp = clientIp;
            this.totalSeq = totalSeq;
            this.chunks = chunks;
            this.lastSendTime = System.currentTimeMillis();
        }

        public String getBatchId() { return batchId; }
        public String getClientIp() { return clientIp; }
        public int getTotalSeq() { return totalSeq; }
        public Map<Integer, String> getChunks() { return chunks; }
        public Set<Integer> getAckedSeqs() { return ackedSeqs; }
        public AtomicInteger getMinAckSeq() { return minAckSeq; }
        public long getLastSendTime() { return lastSendTime; }
        public void setLastSendTime(long lastSendTime) { this.lastSendTime = lastSendTime; }
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    }

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

        // 每2秒检测未确认的 sync_users 分片滑动窗口及积压的 xray_node_command，自动补推
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                // 1. sync_users 内存分片滑动窗口超时重传
                long now = System.currentTimeMillis();
                for (Map.Entry<String, SyncBatch> entry : activeSyncBatches.entrySet()) {
                    SyncBatch batch = entry.getValue();
                    if (now - batch.getLastSendTime() >= 2000) {
                        XrayPartnerWebSocketSession session;
                        synchronized (this) {
                            session = sessionMap.get(batch.getClientIp());
                        }
                        if (session != null && session.isOpen()) {
                            if (batch.getRetryCount() < 5) {
                                batch.setRetryCount(batch.getRetryCount() + 1);
                                batch.setLastSendTime(now);
                                int startSeq = batch.getMinAckSeq().get() + 1;
                                log.warn("[SyncBatch] Resending sync_users chunks for clientIp: {}, batchId: {}, from seq: {} to {}, retry: {}",
                                        batch.getClientIp(), batch.getBatchId(), startSeq, batch.getTotalSeq(), batch.getRetryCount());
                                for (int s = startSeq; s <= batch.getTotalSeq(); s++) {
                                    if (!batch.getAckedSeqs().contains(s)) {
                                        String chunkPayload = batch.getChunks().get(s);
                                        if (chunkPayload != null) {
                                            try {
                                                session.sendMessage(new TextMessage(chunkPayload));
                                            } catch (Exception e) {
                                                log.error("Failed to resend sync chunk seq: {}", s, e);
                                            }
                                        }
                                    }
                                }
                            } else {
                                log.error("[SyncBatch] Batch {} for clientIp {} exceeded max retries, discarded.", batch.getBatchId(), batch.getClientIp());
                                activeSyncBatches.remove(entry.getKey());
                            }
                        }
                    }
                }

                // 2. xray_node_command 持久化发件箱重发 (针对在线节点未ACK的指令)
                if (nodeCommandMapper != null) {
                    List<XrayNodeCommand> pendingList = nodeCommandMapper.selectPendingForRetry();
                    if (pendingList != null && !pendingList.isEmpty()) {
                        for (XrayNodeCommand cmd : pendingList) {
                            XrayPartnerWebSocketSession session;
                            synchronized (this) {
                                session = sessionMap.get(cmd.getClientIp());
                            }
                            if (session != null && session.isOpen()) {
                                try {
                                    JSONObject msgObj = new JSONObject();
                                    msgObj.put("type", cmd.getCommandType());
                                    msgObj.put("msg_id", cmd.getMsgId());
                                    msgObj.put("data", JSON.parse(cmd.getPayload()));
                                    session.sendMessage(new TextMessage(msgObj.toJSONString()));
                                    if (cmd.getRetryCount() != null && cmd.getRetryCount() >= 4) {
                                        nodeCommandMapper.markFailed(cmd.getId());
                                        log.warn("[Outbox] Command id: {}, msgId: {} reached max retries, marked as failed.",
                                                cmd.getId(), cmd.getMsgId());
                                    } else {
                                        nodeCommandMapper.updateRetry(cmd.getId());
                                        log.info("[Outbox] Resent unacked command id: {}, msgId: {} to clientIp: {}",
                                                cmd.getId(), cmd.getMsgId(), cmd.getClientIp());
                                    }
                                } catch (Exception ex) {
                                    log.error("Failed to resend command id: {}", cmd.getId(), ex);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                log.error("Error in reliable delivery retry schedule task", t);
            }
        }, 2, 2, TimeUnit.SECONDS);

        // 每天定时自洁7天前已ACK归档的发件箱指令
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                if (nodeCommandMapper != null) {
                    int cleaned = nodeCommandMapper.cleanExpiredCommands();
                    if (cleaned > 0) {
                        log.info("[Outbox] Cleaned {} expired acknowledged commands.", cleaned);
                    }
                }
            } catch (Exception e) {
                log.error("Error cleaning expired outbox commands", e);
            }
        }, 1, 24, TimeUnit.HOURS);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            applicationContext.publishEvent(new MsgEvent(this, "sync_users", null, null));
        }, 1, 10, TimeUnit.MINUTES);
    }

    @Autowired
    private MsgService msgService;
    @Autowired
    private XrayUserMapper xrayUserMapper;
    @Autowired
    private XrayNodeCommandMapper nodeCommandMapper;
    @Autowired
    private VpnLinesMapper vpnLinesMapper;

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
            // 准入以流量配额为准：用尽即从节点摘除，不再看到期日。
            userData.setOp(user.isTrafficExhausted() ? "remove" : "add");
            return userData;
        }

        public static UserData fromWithOutOp(XrayUser user){
            UserData userData = new UserData();
            userData.setUser_id(user.getUuid());
            userData.setEmail(user.getUniqueEmail());
            return userData;
        }
    }

    private String extractClientIp(WebSocketSession session) {
        // 1. 优先从 URL Query 参数解析节点自报公网IP (例如 ws://host:port/ws?client_ip=1.2.3.4)
        if (session.getUri() != null && session.getUri().getQuery() != null) {
            String query = session.getUri().getQuery();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "client_ip".equalsIgnoreCase(pair[0]) && StringUtils.isNotBlank(pair[1])) {
                    return pair[1].trim();
                }
            }
        }
        // 2. 其次从反向代理 Header 中解析
        if (session.getHandshakeHeaders() != null) {
            String forwarded = session.getHandshakeHeaders().getFirst("X-Forwarded-For");
            if (StringUtils.isNotBlank(forwarded)) {
                return forwarded.split(",")[0].trim();
            }
            String realIp = session.getHandshakeHeaders().getFirst("X-Real-IP");
            if (StringUtils.isNotBlank(realIp)) {
                return realIp.trim();
            }
        }
        // 3. 兜底取底层 TCP remoteAddress
        if (session.getRemoteAddress() != null && session.getRemoteAddress().getAddress() != null) {
            return session.getRemoteAddress().getAddress().getHostAddress();
        }
        return "127.0.0.1";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientIp = extractClientIp(session);
        session.getAttributes().put("clientIp", clientIp);
        log.info("XrayPartnerWebSocketHandler connected clientIp : {}", clientIp);
        XrayPartnerWebSocketSession partnerSession = new XrayPartnerWebSocketSession(session);
        synchronized (this){
            sessionMap.put(clientIp, partnerSession);
        }

        // 1. 投递该节点在断线期间积压的待确认指令 (Transactional Outbox pending replay)
        try {
            if (nodeCommandMapper != null) {
                List<XrayNodeCommand> pendingList = nodeCommandMapper.selectPendingByClientIp(clientIp);
                if (pendingList != null && !pendingList.isEmpty()) {
                    log.info("Flushing {} pending commands to reconnected clientIp: {}", pendingList.size(), clientIp);
                    for (XrayNodeCommand cmd : pendingList) {
                        JSONObject msgObj = new JSONObject();
                        msgObj.put("type", cmd.getCommandType());
                        msgObj.put("msg_id", cmd.getMsgId());
                        msgObj.put("data", JSON.parse(cmd.getPayload()));
                        partnerSession.sendMessage(new TextMessage(msgObj.toJSONString()));
                        nodeCommandMapper.updateRetry(cmd.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error flushing pending commands on connect for clientIp: {}", clientIp, e);
        }

        // 2. 触发一次全量用户同步基线对齐
        applicationContext.publishEvent(new MsgEvent(this, "sync_users", clientIp, null));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        String clientIp = (String) session.getAttributes().get("clientIp");
        if (StringUtils.isBlank(clientIp)) {
            clientIp = extractClientIp(session);
        }
        log.debug("XrayPartnerWebSocketHandler receive msg : {}, clientId : {}", msg, clientIp);
        XrayPartnerWebSocketSession xrayPartnerWebSocketSession;
        synchronized (this){
            xrayPartnerWebSocketSession = sessionMap.get(clientIp);
        }
        if(xrayPartnerWebSocketSession != null){
            xrayPartnerWebSocketSession.updateLastReadTime();
            JSONObject msgObj = JSON.parseObject(msg);
            String type = msgObj.getString("type");

            // 1. 处理节点对 add_user / remove_user 的单播 ACK
            if ("ack".equals(type)) {
                String msgId = msgObj.getString("msg_id");
                if (StringUtils.isNotBlank(msgId) && nodeCommandMapper != null) {
                    nodeCommandMapper.markAcked(msgId, clientIp);
                    log.info("XrayPartnerWebSocketHandler command ACKed: msgId={}, clientIp={}", msgId, clientIp);
                }
                return;
            }

            // 2. 处理节点对 sync_users 分片的 ACK (滑动窗口推进)
            if ("sync_ack".equals(type)) {
                String batchId = msgObj.getString("batch_id");
                Integer seq = msgObj.getInteger("seq");
                if (batchId != null && seq != null) {
                    SyncBatch batch = activeSyncBatches.get(clientIp);
                    if (batch != null && batchId.equals(batch.getBatchId())) {
                        batch.getAckedSeqs().add(seq);
                        while (batch.getAckedSeqs().contains(batch.getMinAckSeq().get() + 1)) {
                            batch.getMinAckSeq().incrementAndGet();
                        }
                        log.debug("sync_ack received: clientIp={}, batchId={}, seq={}, minAckSeq={}",
                                clientIp, batchId, seq, batch.getMinAckSeq().get());
                        if (batch.getMinAckSeq().get() >= batch.getTotalSeq()) {
                            log.info("sync_users batch {} completely ACKed by clientIp: {}", batchId, clientIp);
                            activeSyncBatches.remove(clientIp);
                        }
                    }
                }
                return;
            }

            // 3. 处理节点上报的 traffic_collect 分片，持久化并回复 traffic_ack
            if ("traffic_collect".equals(type)) {
                msgService.saveTrafficCollectList(clientIp, msgObj);
                String batchId = msgObj.getString("batch_id");
                Integer seq = msgObj.getInteger("seq");
                if (batchId != null && seq != null) {
                    JSONObject ackObj = new JSONObject();
                    ackObj.put("type", "traffic_ack");
                    ackObj.put("batch_id", batchId);
                    ackObj.put("seq", seq);
                    try {
                        xrayPartnerWebSocketSession.sendMessage(new TextMessage(ackObj.toJSONString()));
                    } catch (Exception ex) {
                        log.error("Failed to send traffic_ack to clientIp: {}", clientIp, ex);
                    }
                }
                return;
            }

            applicationContext.publishEvent(new MsgEvent(this, type, clientIp, msgObj));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String clientIp = (String) session.getAttributes().get("clientIp");
        if (StringUtils.isBlank(clientIp)) {
            clientIp = extractClientIp(session);
        }
        log.info("XrayPartnerWebSocketHandler connection closed, clientId : {}", clientIp);
        synchronized (this){
            sessionMap.remove(clientIp);
        }
        activeSyncBatches.remove(clientIp);
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
                sendSyncUsers(clientIp);
                break;
            case "add_user":
            case "remove_user":
                sendUserCommand(type, (XrayUser) event.getMsg());
                break;
        }
    }

    private void sendSyncUsers(String clientIp) {
        List<XrayUser> xrayUserList = xrayUserMapper.selectXrayUserList(new XrayUser());
        List<UserData> userDataList = xrayUserList.stream().map(UserData::from).collect(Collectors.toList());
        int chunkSize = 100;
        int totalSeq = (userDataList.size() + chunkSize - 1) / chunkSize;
        if (totalSeq == 0) totalSeq = 1;

        Map<String, XrayPartnerWebSocketSession> targets;
        if (StringUtils.isNotBlank(clientIp)) {
            targets = new HashMap<>(1);
            synchronized (this) {
                if (sessionMap.containsKey(clientIp)) {
                    targets.put(clientIp, sessionMap.get(clientIp));
                }
            }
        } else {
            targets = dumpSessions();
        }

        for (Map.Entry<String, XrayPartnerWebSocketSession> entry : targets.entrySet()) {
            String targetIp = entry.getKey();
            XrayPartnerWebSocketSession session = entry.getValue();
            if (session == null || !session.isOpen()) continue;

            String batchId = UUID.randomUUID().toString();
            Map<Integer, String> chunkMap = new HashMap<>();
            for (int i = 0; i < totalSeq; i++) {
                int seq = i + 1;
                int fromIndex = i * chunkSize;
                int toIndex = Math.min(fromIndex + chunkSize, userDataList.size());
                List<UserData> subList = userDataList.subList(fromIndex, toIndex);

                JSONObject chunkMsg = new JSONObject();
                chunkMsg.put("type", "sync_users");
                chunkMsg.put("batch_id", batchId);
                chunkMsg.put("seq", seq);
                chunkMsg.put("total_seq", totalSeq);
                chunkMsg.put("data", subList);
                chunkMap.put(seq, chunkMsg.toJSONString());
            }

            SyncBatch batch = new SyncBatch(batchId, targetIp, totalSeq, chunkMap);
            activeSyncBatches.put(targetIp, batch);

            for (int seq = 1; seq <= totalSeq; seq++) {
                try {
                    session.sendMessage(new TextMessage(chunkMap.get(seq)));
                } catch (Exception e) {
                    log.error("Failed to send sync chunk {} to {}", seq, targetIp, e);
                }
            }
        }
    }

    private void sendUserCommand(String type, XrayUser user) {
        if (user == null) return;
        UserData userData = UserData.fromWithOutOp(user);
        String payloadStr = JSON.toJSONString(userData);
        String msgId = UUID.randomUUID().toString();

        Set<String> targetIps = new HashSet<>();
        try {
            if (vpnLinesMapper != null) {
                List<VpnLines> lines = vpnLinesMapper.selectVpnLinesList(new VpnLines());
                if (lines != null) {
                    for (VpnLines line : lines) {
                        if (StringUtils.isNotBlank(line.getIp())) {
                            targetIps.add(line.getIp().trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to query vpn lines for outbox command", e);
        }
        synchronized (this) {
            targetIps.addAll(sessionMap.keySet());
        }

        JSONObject pushMsg = new JSONObject();
        pushMsg.put("type", type);
        pushMsg.put("msg_id", msgId);
        pushMsg.put("data", userData);
        String pushPayload = pushMsg.toJSONString();

        for (String targetIp : targetIps) {
            if (StringUtils.isBlank(targetIp)) continue;
            try {
                if (nodeCommandMapper != null) {
                    XrayNodeCommand cmd = new XrayNodeCommand();
                    cmd.setMsgId(msgId);
                    cmd.setClientIp(targetIp);
                    cmd.setCommandType(type);
                    cmd.setPayload(payloadStr);
                    nodeCommandMapper.insertCommand(cmd);
                }
            } catch (Exception ex) {
                log.error("Failed to insert xray_node_command for clientIp: {}", targetIp, ex);
            }

            XrayPartnerWebSocketSession session;
            synchronized (this) {
                session = sessionMap.get(targetIp);
            }
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(pushPayload));
                } catch (Exception ex) {
                    log.error("Fast-path send command failed for clientIp: {}", targetIp, ex);
                }
            }
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

