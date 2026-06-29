package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.XrayUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户管理Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-11
 */
public interface XrayUserMapper 
{

    int updateTrafficByEmail(@Param("email") String email, @Param("usedTraffic") Integer usedTraffic);
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
     * 删除用户管理
     * 
     * @param id 用户管理主键
     * @return 结果
     */
    public int deleteXrayUserById(Long id);

    /**
     * 批量删除用户管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteXrayUserByIds(String[] ids);

    public List<XrayUser> selectXrayUserListV2(@Param("search") String search, @Param("belong") String belong, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    XrayUser findByProviderUserId(@Param("provider") String provider, @Param("providerUserId") String providerUserId);
}
