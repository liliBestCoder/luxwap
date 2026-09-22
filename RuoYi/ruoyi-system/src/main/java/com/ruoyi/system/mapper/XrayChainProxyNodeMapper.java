package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayChainProxyNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 链式代理上游端点Mapper接口
 *
 * @author ruoyi
 */
public interface XrayChainProxyNodeMapper {

    /**
     * 查询上游端点信息
     */
    XrayChainProxyNode selectXrayChainProxyNodeById(Long id);

    /**
     * 根据业务安全唯一Key查询上游端点信息
     */
    XrayChainProxyNode selectXrayChainProxyNodeByKey(@Param("nodeKey") String nodeKey);

    /**
     * 查询上游端点列表（用户私有 + 平台公共端点）
     */
    List<XrayChainProxyNode> selectXrayChainProxyNodeList(XrayChainProxyNode node);

    /**
     * 根据用户ID和Host+Port检查是否已存在相同端点
     */
    XrayChainProxyNode checkNodeExists(@Param("userId") Long userId,
                                       @Param("host") String host,
                                       @Param("port") Integer port);

    /**
     * 新增上游端点
     */
    int insertXrayChainProxyNode(XrayChainProxyNode node);

    /**
     * 批量新增上游端点
     */
    int batchInsertXrayChainProxyNode(@Param("list") List<XrayChainProxyNode> list);

    /**
     * 修改上游端点
     */
    int updateXrayChainProxyNode(XrayChainProxyNode node);

    /**
     * 单个删除上游端点（逻辑删除）
     */
    int deleteXrayChainProxyNodeById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 根据业务安全Key单个删除上游端点（逻辑删除）
     */
    int deleteXrayChainProxyNodeByKey(@Param("nodeKey") String nodeKey, @Param("userId") Long userId);

    /**
     * 批量删除上游端点（逻辑删除）
     */
    int deleteXrayChainProxyNodeByIds(@Param("ids") Long[] ids, @Param("userId") Long userId);

    /**
     * 根据业务安全Key批量删除上游端点（逻辑删除）
     */
    int deleteXrayChainProxyNodeByKeys(@Param("nodeKeys") String[] nodeKeys, @Param("userId") Long userId);
}
