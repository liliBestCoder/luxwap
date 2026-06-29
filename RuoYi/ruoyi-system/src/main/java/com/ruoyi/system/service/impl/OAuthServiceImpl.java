package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.*;
import com.ruoyi.system.event.MsgEvent;
import com.ruoyi.system.mapper.XrayUserMapper;
import com.ruoyi.system.mapper.XrayUserSessionMapper;
import com.ruoyi.system.service.IXrayUserService;
import com.ruoyi.system.service.Ip2regionService;
import com.ruoyi.system.service.OAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OAuthServiceImpl implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(OAuthServiceImpl.class);
    @Autowired
    private Map<String, OAuthProvider> providers;

    @Autowired
    private XrayUserMapper userMapper;

    @Autowired
    private XrayUserSessionMapper sessionMapper;

    private Map<String, OAuthTask> taskStore = new ConcurrentHashMap<>();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private IXrayUserService userService;
    @Autowired
    private Ip2regionService ip2regionService;

    private ScheduledExecutorService scheduler;

    private final Lock taskStoreLock = new ReentrantLock();

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTasks, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void destroy() throws Exception {
        if(scheduler != null){
            scheduler.shutdown();
        }
    }

    private void cleanupExpiredTasks() {
        try{
            taskStoreLock.lock();
            Iterator<Map.Entry<String, OAuthTask>> iterator = taskStore.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, OAuthTask> entry = iterator.next();
                OAuthTask task = entry.getValue();
                if (task.isExpired()) {
                    iterator.remove();
                }
            }
        }catch (Exception e){
            log.error("cleanupExpiredTasks error", e);
        }finally {
            taskStoreLock.unlock();
        }
    }

    public OAuthTaskResponse createLoginTask(String clientIp, OAuthTaskRequest req) {
        String taskId = UUID.randomUUID().toString();
        OAuthProvider provider = providers.get(req.getProvider() + "OAuthProvider");
        String authUrl = provider.getAuthUrl(taskId);

        String countryCode = ip2regionService.getCountryCode(clientIp);

        OAuthTask task = new OAuthTask(taskId, countryCode, req);
        try{
            taskStoreLock.lock();
            taskStore.put(taskId, task);
        }finally {
            taskStoreLock.unlock();
        }

        return new OAuthTaskResponse(taskId, authUrl);
    }

    public void handleCallback(String code, String state) throws Exception {
        OAuthTask task = null;
        try{
            taskStoreLock.lock();
            task = taskStore.get(state);
        }finally {
            taskStoreLock.unlock();
        }

        String platform = task.getRequest().getProvider();
        OAuthProvider provider = providers.get(platform + "OAuthProvider");
        OAuthUserInfo userInfo = provider.exchangeCodeForUser(code);

        try{
            XrayUser user = registerThirdPartyUser(platform, userInfo, task);
            String token = userService.login(user, task.getRequest().getDeviceId(), task.getRequest().getOs(), task.getRequest().getDeviceType(), task.getRequest().getDeviceName());
            task.setStatus("success");
            task.setToken(token);
        }catch (RuntimeException e){
            task.setStatus("failed");
            task.setMsg(e.getMessage());
        }
    }

    private XrayUser registerThirdPartyUser(String platform, OAuthUserInfo userInfo, OAuthTask task) {
        XrayUser user = userMapper.findByProviderUserId(platform, userInfo.getProviderUserId());
        if (user != null) {
            return user;
        }
        return registerThirdPartyUser(userInfo,  task);
    }

    private XrayUser registerThirdPartyUser(OAuthUserInfo info, OAuthTask task) {
        XrayUser queryCondition = new XrayUser();
        queryCondition.setUsername(info.getEmail());

        List<XrayUser> xrayUserList = userMapper.selectXrayUserList(queryCondition);
        if(!CollectionUtils.isEmpty(xrayUserList)){
            throw new RuntimeException("邮箱已注册");
        }

        queryCondition = new XrayUser();
        queryCondition.setDeviceId(task.getRequest().getDeviceId());
        xrayUserList = userMapper.selectXrayUserList(queryCondition);
        if(!CollectionUtils.isEmpty(xrayUserList)){
            throw new RuntimeException("该设备已注册");
        }

        XrayUser user = new XrayUser();
        String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        user.setUsername(info.getEmail());
        user.setEmail(info.getEmail());
        user.setUniqueEmail(uuid + "@luxwap.com");
        user.setCountry(task.getCountryCode());
        user.setNick(info.getName());
        user.setProvider(info.getProvider());
        user.setProviderUserId(info.getProviderUserId());
        user.setProviderUsername(info.getName());
        user.setProviderAvatarUrl(info.getAvatarUrl());
        user.setDeviceId(task.getRequest().getDeviceId());
        user.setStatus(0);
        user.setUsedTraffic(0L);
        user.setCumulativeMonths(0);
        user.setExpiration(Date.from(LocalDateTime.now().plusDays(3).atZone(ZoneId.systemDefault()).toInstant()));
        user.setType("outer");
        userMapper.insertXrayUser(user);
        //注册追加user
        applicationContext.publishEvent(new MsgEvent(this, "add_user", null, user));
        return user;
    }

    public OAuthTaskResult getTaskResult(String taskId) {
        OAuthTask task = null;
        try{
            taskStoreLock.lock();
            task = taskStore.get(taskId);
        }finally {
            taskStoreLock.unlock();
        }
        if (task == null){
            return new OAuthTaskResult("pending", null,  null);
        }
        if (task.isExpired()) {
            return new OAuthTaskResult("expired", null, "Task has expired");
        }
        return new OAuthTaskResult(task.getStatus(), task.getToken(), task.getMsg());
    }
}

