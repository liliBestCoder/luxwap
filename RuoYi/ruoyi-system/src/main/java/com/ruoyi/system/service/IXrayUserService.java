package com.ruoyi.system.service;

import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.domain.XrayUserSession;

/**
 * 用户管理Service接口
 * 
 * @author ruoyi
 * @date 2025-08-11
 */
public interface IXrayUserService 
{
    /**
     * 查询用户管理
     * 
     * @param id 用户管理主键
     * @return 用户管理
     */
    public XrayUser selectXrayUserById(Long id);

    /**
     * 查询用户管理列表
     * 
     * @param xrayUser 用户管理
     * @return 用户管理集合
     */
    public List<XrayUser> selectXrayUserList(XrayUser xrayUser);

    public List<XrayUser> selectXrayUserListV2(String search, String belong, Date startTime, Date endTime);

    /**
     * 新增用户管理
     * 
     * @param xrayUser 用户管理
     * @return 结果
     */
    public int insertXrayUser(XrayUser xrayUser);

    /**
     * 修改用户管理
     * 
     * @param xrayUser 用户管理
     * @return 结果
     */
    public int updateXrayUser(XrayUser xrayUser);

    /**
     * 批量删除用户管理
     * 
     * @param ids 需要删除的用户管理主键集合
     * @return 结果
     */
    public int deleteXrayUserByIds(String ids);

    /**
     * 删除用户管理信息
     * 
     * @param id 用户管理主键
     * @return 结果
     */
    public int deleteXrayUserById(Long id);

    public void register(String username, String password, String email, String inviteCode, String clientIp, String deviceId);

    public String login(String username, String password, String deviceId, String os, String deviceType, String deviceName) throws Exception;
    public String login(XrayUser user, String deviceId, String os, String deviceType, String deviceName) throws Exception;

    public List<XrayUserSession> getOnlineDevices(Long userId);

    int disable(Long userId);

    public String changePassword(Long userId, String oldPassword, String newPassword);

    public void resetPassword(String email, String newPassword, String verifyCode);
}
