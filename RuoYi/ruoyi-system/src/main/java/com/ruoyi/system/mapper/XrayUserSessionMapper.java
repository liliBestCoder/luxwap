package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.XrayUserSession;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
public interface XrayUserSessionMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public XrayUserSession selectXrayUserSessionById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param xrayUserSession 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<XrayUserSession> selectXrayUserSessionList(XrayUserSession xrayUserSession);

    /**
     * 新增【请填写功能名称】
     * 
     * @param xrayUserSession 【请填写功能名称】
     * @return 结果
     */
    public int insertXrayUserSession(XrayUserSession xrayUserSession);

    /**
     * 修改【请填写功能名称】
     * 
     * @param xrayUserSession 【请填写功能名称】
     * @return 结果
     */
    public int updateXrayUserSession(XrayUserSession xrayUserSession);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteXrayUserSessionById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteXrayUserSessionByIds(String[] ids);

    @Select("SELECT COUNT(*) FROM xray_user_session WHERE user_id = #{userId} AND token_expiration > now()")
    public int countLoggedInDevices(@Param("userId") Long userId);

    List<XrayUserSession> getActiveDevicesByUserId(@Param("userId") Long userId);
}
