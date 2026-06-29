package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.system.domain.XrayInnerUser;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.XrayInnerUserMapper;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.IXrayInnerUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class XrayInnerUserServiceImpl implements IXrayInnerUserService {

    @Autowired
    private XrayInnerUserMapper xrayInnerUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private ISysUserService sysUserService;

    @Override
    public XrayInnerUser selectXrayInnerUserById(Long id) {
        return xrayInnerUserMapper.selectXrayInnerUserById(id);
    }

    @Override
    public List<XrayInnerUser> selectXrayInnerUserLikeList(XrayInnerUser xrayInnerUser) {
        return xrayInnerUserMapper.selectXrayInnerUserLikeList(xrayInnerUser);
    }

    @Override
    @Transactional
    public int insertXrayInnerUser(XrayInnerUser xrayInnerUser, String currentUser) {
        String userName = xrayInnerUser.getUserName();
        String password = xrayInnerUser.getPassword();
        String type = xrayInnerUser.getType();

        if (StringUtils.isBlank(userName)) {
            throw new IllegalArgumentException("用户名不能为空!");
        }

        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("密码不能为空!");
        }

        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("用户类型不能为空!");
        }

        if (!"admin-slave".equals(type) &&  !"support".equals(type)) {
            throw new IllegalArgumentException("用户类型不正确!");
        }

        // 1. 检查用户名是否已存在
        XrayInnerUser existUser = new XrayInnerUser();
        existUser.setUserName(xrayInnerUser.getUserName());
        List<XrayInnerUser> existList = xrayInnerUserMapper.selectXrayInnerUserList(existUser);
        if (!CollectionUtils.isEmpty(existList)) {
            // 可以抛异常或者返回 0 表示失败
            throw new IllegalArgumentException("用户名已存在：" + xrayInnerUser.getUserName());
        }

        // 3. 插入
        SysRole role = sysRoleMapper.checkRoleKeyUnique(type);
        SysUser user = new SysUser();
        user.setLoginName(userName);
        user.setUserName(userName);
        user.setPassword(password);
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setCreateBy(currentUser);
        user.setRoleIds(new Long[]{role.getRoleId()});
        sysUserService.insertUser(user);

        xrayInnerUser.setUserId(user.getUserId());
        xrayInnerUser.setDeleted(0);
        //0停用 1正常
        xrayInnerUser.setStatus(1);
        return xrayInnerUserMapper.insertXrayInnerUser(xrayInnerUser);
    }

    private String encryptPassword(String loginName, String password, String salt)
    {
        return new Md5Hash(loginName + password + salt).toHex();
    }


    @Override
    public int updateXrayInnerUser(XrayInnerUser xrayInnerUser) {
        return xrayInnerUserMapper.updateXrayInnerUser(xrayInnerUser);
    }

    @Override
    public int deleteXrayInnerUserById(Long id) {
        XrayInnerUser xrayInnerUser = xrayInnerUserMapper.selectXrayInnerUserById(id);
        if (xrayInnerUser == null){
            throw  new IllegalArgumentException("用户不存在!");
        }
        sysUserService.deleteUserById(xrayInnerUser.getUserId());
        return xrayInnerUserMapper.deleteXrayInnerUserById(id);
    }

    @Override
    public int diableXrayInnerUserById(Long id) {
        XrayInnerUser xrayInnerUser = xrayInnerUserMapper.selectXrayInnerUserById(id);
        if (xrayInnerUser == null){
            throw  new IllegalArgumentException("用户不存在!");
        }
        XrayInnerUser updateXrayInnerUser = new XrayInnerUser();
        updateXrayInnerUser.setId(id);
        updateXrayInnerUser.setStatus(xrayInnerUser.getStatus() == 0 ? 1 : 0);
        xrayInnerUserMapper.updateXrayInnerUser(updateXrayInnerUser);

        Long userId = xrayInnerUser.getUserId();
        SysUser updateSysUser = new SysUser();
        updateSysUser.setUserId(userId);
        updateSysUser.setStatus(updateXrayInnerUser.getStatus() == 0 ? "1" : "0");

        return sysUserService.updateUser(updateSysUser);
    }
}

