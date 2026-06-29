package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayInnerUser;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 内部用户Mapper接口
 */
@Mapper
public interface XrayInnerUserMapper {

    /**
     * 查询内部用户
     */
    XrayInnerUser selectXrayInnerUserById(Long id);

    /**
     * 查询内部用户列表（支持 user_name 模糊查询，type 精确查询）
     */
    List<XrayInnerUser> selectXrayInnerUserLikeList(XrayInnerUser xrayInnerUser);

    /**
     * 查询内部用户列表（支持 user_name 模糊查询，type 精确查询）
     */
    List<XrayInnerUser> selectXrayInnerUserList(XrayInnerUser xrayInnerUser);

    /**
     * 新增内部用户
     */
    int insertXrayInnerUser(XrayInnerUser xrayInnerUser);

    /**
     * 修改内部用户
     */
    int updateXrayInnerUser(XrayInnerUser xrayInnerUser);

    /**
     * 逻辑删除
     */
    int deleteXrayInnerUserById(Long id);

    /**
     * 批量逻辑删除
     */
    int deleteXrayInnerUserByIds(Long[] ids);
}

