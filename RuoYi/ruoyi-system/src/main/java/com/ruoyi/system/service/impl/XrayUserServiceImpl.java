package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.controller.XrayDistributorsController;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.domain.XrayUserSession;
import com.ruoyi.system.event.MsgEvent;
import com.ruoyi.system.mapper.XrayUserMapper;
import com.ruoyi.system.mapper.XrayUserSessionMapper;
import com.ruoyi.system.service.EmailCodeService;
import com.ruoyi.system.service.IXrayUserService;
import com.ruoyi.system.service.Ip2regionService;
import com.ruoyi.system.util.AESJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 用户管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-11
 */
@Service
public class XrayUserServiceImpl implements IXrayUserService 
{
    @Autowired
    private XrayUserMapper xrayUserMapper;
    @Autowired
    private XrayUserSessionMapper xrayUserSessionMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Ip2regionService ip2regionService;
    @Autowired
    private EmailCodeService emailCodeService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 查询用户管理
     * 
     * @param id 用户管理主键
     * @return 用户管理
     */
    @Override
    public XrayUser selectXrayUserById(Long id)
    {
        return xrayUserMapper.selectXrayUserById(id);
    }

    /**
     * 查询用户管理列表
     * 
     * @param xrayUser 用户管理
     * @return 用户管理
     */
    @Override
    public List<XrayUser> selectXrayUserList(XrayUser xrayUser)
    {
        return xrayUserMapper.selectXrayUserList(xrayUser);
    }

    public List<XrayUser> selectXrayUserListV2(String search, String belong, Date startTime, Date endTime){
        return xrayUserMapper.selectXrayUserListV2(search, belong, startTime, endTime);
    }

    /**
     * 新增用户管理
     * 
     * @param xrayUser 用户管理
     * @return 结果
     */
    @Override
    public int insertXrayUser(XrayUser xrayUser)
    {
        return xrayUserMapper.insertXrayUser(xrayUser);
    }

    /**
     * 修改用户管理
     * 
     * @param xrayUser 用户管理
     * @return 结果
     */
    @Override
    public int updateXrayUser(XrayUser xrayUser)
    {
        return xrayUserMapper.updateXrayUser(xrayUser);
    }

    /**
     * 批量删除用户管理
     * 
     * @param ids 需要删除的用户管理主键
     * @return 结果
     */
    @Override
    public int deleteXrayUserByIds(String ids)
    {
        return xrayUserMapper.deleteXrayUserByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除用户管理信息
     * 
     * @param id 用户管理主键
     * @return 结果
     */
    @Override
    public int deleteXrayUserById(Long id)
    {
        return xrayUserMapper.deleteXrayUserById(id);
    }

    // 注册
    public void register(String username, String password, String email, String inviteCode, String clientIp, String deviceId) {
        if (StringUtils.isBlank(username)) {
            throw new RuntimeException("注册邮箱不能为空!");
        }
        if (StringUtils.isBlank(password)) {
            throw new RuntimeException("密码不能为空!");
        }
        if (StringUtils.isBlank(deviceId)) {
            throw new RuntimeException("设备ID不能为空！");
        }
        String country = ip2regionService.getCountryCode(clientIp).toUpperCase();

        XrayUser queryCondition = new XrayUser();
        queryCondition.setUsername(username);

        List<XrayUser> xrayUserList = xrayUserMapper.selectXrayUserList(queryCondition);
        if(!CollectionUtils.isEmpty(xrayUserList)){
            throw new RuntimeException("邮箱已注册");
        }

        queryCondition = new XrayUser();
        queryCondition.setDeviceId(deviceId);
        xrayUserList = xrayUserMapper.selectXrayUserList(queryCondition);
        if(!CollectionUtils.isEmpty(xrayUserList)){
            throw new RuntimeException("该设备已注册");
        }

        XrayUser user = new XrayUser();
        String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        user.setUsername(username);
        user.setDeviceId(deviceId);
        user.setNick("U" + XrayDistributorsController.BindingCodeGenerator.generateUniqueBindingCode());
        user.setCountry(country);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setUniqueEmail(uuid + "@luxwap.com");
        user.setStatus(0);
        user.setUsedTraffic(0L);
        user.setCumulativeMonths(0);
        boolean invited = StringUtils.isNotBlank(inviteCode);
        if(invited){
            user.setInviteCode(inviteCode);
            user.setType("outer");
        }else{
            user.setType("inner");
        }

        //首次注册 送3天的免费试用
        user.setExpiration(Date.from(LocalDateTime.now().plusDays(3).atZone(ZoneId.systemDefault()).toInstant()));
        xrayUserMapper.insertXrayUser(user);
        //注册追加user
        applicationContext.publishEvent(new MsgEvent(this, "add_user", null, user));
    }

    public String changePassword(Long userId, String oldPassword, String newPassword) {
        if(StringUtils.isBlank(oldPassword)){
            throw new RuntimeException("原密码为空!");
        }
        if(StringUtils.isBlank(newPassword)){
            throw new RuntimeException("新密码为空!");
        }

        XrayUser user = xrayUserMapper.selectXrayUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在!");
        }

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new RuntimeException("原密码错误!");
        }

        XrayUser update = new XrayUser();
        update.setId(userId);
        update.setPassword(passwordEncoder.encode(newPassword));
        xrayUserMapper.updateXrayUser(update);
        return update.getPassword();
    }


    public void resetPassword(String email, String newPassword, String verifyCode){
        if(StringUtils.isBlank(email)){
            throw new RuntimeException("邮箱为空!");
        }
        if(StringUtils.isBlank(newPassword)){
            throw new RuntimeException("密码为空!");
        }
        if(StringUtils.isBlank(verifyCode)){
            throw new RuntimeException("验证码为空!");
        }

        XrayUser query = new XrayUser();
        query.setUsername(email);

        List<XrayUser> xrayUsers = xrayUserMapper.selectXrayUserList(query);
        if (CollectionUtils.isEmpty(xrayUsers)) {
            throw new RuntimeException("用户不存在!");
        }

        if(!emailCodeService.verifyCode(email, verifyCode)){
            throw new RuntimeException("验证码错误!");
        }

        XrayUser update = new XrayUser();
        update.setId(xrayUsers.get(0).getId());
        update.setPassword(passwordEncoder.encode(newPassword));
        xrayUserMapper.updateXrayUser(update);
    }

    public String login(XrayUser user, String deviceId, String os, String deviceType, String deviceName) throws Exception {
        Long userId = user.getId();
        String currentDeviceSessionToken = getCurrentDeviceSessionToken(userId, deviceId);
        if(StringUtils.isNotBlank(currentDeviceSessionToken)){
            return currentDeviceSessionToken;
        }

        int loggedInDeviceCount = xrayUserSessionMapper.countLoggedInDevices(user.getId());
        if (loggedInDeviceCount >= 2) {
            // 获取当前在线设备
            List<XrayUserSession> onlineDevices = getOnlineDevices(user.getId());
            if (!onlineDevices.isEmpty()) {
                // 随机选择一个设备踢下线
                Random random = new Random();
                XrayUserSession cancelSession = onlineDevices.get(random.nextInt(onlineDevices.size()));
                XrayUserSession cancelSessionUpdate = new XrayUserSession();
                cancelSessionUpdate.setId(cancelSession.getId());
                cancelSessionUpdate.setStatus(1);
                xrayUserSessionMapper.updateXrayUserSession(cancelSessionUpdate);
            }
        }

        long expirationMillis = 24 * 60 * 60 * 1000; // 1 天
        String token = AESJwtUtil.encryptUuid(user.getUuid(), user.getId(), expirationMillis);
        Date expiration = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        boolean expired = expiration.getTime() < System.currentTimeMillis();
        //发送消息
        if(expired){
            //过期删除user
            applicationContext.publishEvent(new MsgEvent(this, "remove_user", null, user));
        }else {
            //没过期追加user
            applicationContext.publishEvent(new MsgEvent(this, "add_user", null, user));
        }

        XrayUserSession session = new XrayUserSession();
        session.setUserId(user.getId());
        session.setDeviceId(deviceId);
        session.setOs(os);
        session.setDeviceType(deviceType);
        session.setDeviceName(deviceName);
        session.setToken(token);
        session.setTokenExpiration(expiration);
        session.setStatus(0);

        try {
            xrayUserSessionMapper.insertXrayUserSession(session);
        } catch (DuplicateKeyException e) {
            // 如果是唯一约束冲突，说明同一设备已登录
            // 重新查询该设备的会话
            currentDeviceSessionToken = getCurrentDeviceSessionToken(userId, deviceId);
            if (StringUtils.isNotBlank(currentDeviceSessionToken)) {
                return currentDeviceSessionToken;
            }
            // 如果还是失败，抛出异常
            throw e;
        }
        return token;
    }

    // 登录
    public String login(String username, String password, String deviceId, String os, String deviceType, String deviceName) throws Exception {
        XrayUser userQueryCondition = new XrayUser();
        userQueryCondition.setUsername(username);

        List<XrayUser> xrayUserList = xrayUserMapper.selectXrayUserList(userQueryCondition);
        XrayUser user;
        if(CollectionUtils.isEmpty(xrayUserList) || !passwordEncoder.matches(password, (user = xrayUserList.get(0)).getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return login(user, deviceId, os, deviceType, deviceName);
    }

    public String getCurrentDeviceSessionToken(Long userId, String deviceId){
        XrayUserSession currentDeviceSessionQueryCondition = new XrayUserSession();
        currentDeviceSessionQueryCondition.setUserId(userId);
        currentDeviceSessionQueryCondition.setStatus(0);
        currentDeviceSessionQueryCondition.setDeviceId(deviceId);
        List<XrayUserSession> retrySessionList = xrayUserSessionMapper.selectXrayUserSessionList(currentDeviceSessionQueryCondition);
        if (!CollectionUtils.isEmpty(retrySessionList)) {
            XrayUserSession retrySession = retrySessionList.get(0);
            boolean expired = retrySession.getTokenExpiration().before(new Date());
            if(!expired){
                return retrySession.getToken();
            }
        }
        return null;
    }

    // 获取当前 uuid 对应的在线设备信息
    public List<XrayUserSession> getOnlineDevices(Long userId) {
        return xrayUserSessionMapper.getActiveDevicesByUserId(userId);
    }

    public int disable(Long userId){
        XrayUser xrayUser = xrayUserMapper.selectXrayUserById(userId);
        if (xrayUser == null){
            throw new RuntimeException("用户不存在");
        }

        int newStatus = xrayUser.getStatus() == 0 ? 1 : 0;
        XrayUser update = new XrayUser();
        update.setId(xrayUser.getId());
        update.setStatus(newStatus);
        int row = xrayUserMapper.updateXrayUser(update);

        if(newStatus == 1){
            applicationContext.publishEvent(new MsgEvent(this, "remove_user", null, xrayUser));
        }else {
            applicationContext.publishEvent(new MsgEvent(this, "add_user", null, xrayUser));
        }
        return row;
    }

}
