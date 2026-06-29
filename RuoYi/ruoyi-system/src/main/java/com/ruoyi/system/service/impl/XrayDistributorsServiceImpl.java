package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.system.domain.XrayDistributorsConfig;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.XrayDistributorsConfigMapper;
import com.ruoyi.system.service.ISysUserService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.XrayDistributorsMapper;
import com.ruoyi.system.domain.XrayDistributors;
import com.ruoyi.system.service.IXrayDistributorsService;
import com.ruoyi.common.core.text.Convert;

/**
 * 经销商Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-21
 */
@Service
public class XrayDistributorsServiceImpl implements IXrayDistributorsService 
{
    @Autowired
    private XrayDistributorsMapper xrayDistributorsMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private XrayDistributorsConfigMapper xrayDistributorsConfigMapper;

    private String encryptPassword(String loginName, String password, String salt)
    {
        return new Md5Hash(loginName + password + salt).toHex();
    }

    /**
     * 查询经销商
     * 
     * @param id 经销商主键
     * @return 经销商
     */
    @Override
    public XrayDistributors selectXrayDistributorsById(Long id)
    {
        return xrayDistributorsMapper.selectXrayDistributorsById(id);
    }

    /**
     * 查询经销商列表
     * 
     * @param xrayDistributors 经销商
     * @return 经销商
     */
    @Override
    public List<XrayDistributors> selectXrayDistributorsList(XrayDistributors xrayDistributors)
    {
        return xrayDistributorsMapper.selectXrayDistributorsList(xrayDistributors);
    }

    /**
     * 新增经销商
     * 
     * @param xrayDistributors 经销商
     * @return 结果
     */
    @Override
    public int insertXrayDistributors(XrayDistributors xrayDistributors)
    {
        return xrayDistributorsMapper.insertXrayDistributors(xrayDistributors);
    }

    /**
     * 修改经销商
     * 
     * @param xrayDistributors 经销商
     * @return 结果
     */
    @Override
    public int updateXrayDistributors(XrayDistributors xrayDistributors, String currentUser)
    {
        int rows = xrayDistributorsMapper.updateXrayDistributors(xrayDistributors);
        String status = xrayDistributors.getStatus();
        if(status.equals("approved") && xrayDistributors.getUserId() == null){
            SysRole distributors = sysRoleMapper.checkRoleKeyUnique("reseller");
            SysUser user = new SysUser();
            user.setLoginName(xrayDistributors.getEmail());
            user.setUserName(xrayDistributors.getContactPerson());
            user.setPassword(xrayDistributors.getEmail());
            user.setSalt(ShiroUtils.randomSalt());
            user.setPassword(encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
            user.setPwdUpdateDate(DateUtils.getNowDate());
            user.setCreateBy(currentUser);
            user.setRoleIds(new Long[]{distributors.getRoleId()});
            sysUserService.insertUser(user);

            XrayDistributors update = new XrayDistributors();
            update.setId(xrayDistributors.getId());
            update.setUserId(user.getUserId());
            xrayDistributorsMapper.updateXrayDistributors(update);
        }
        return rows;
    }

    /**
     * 批量删除经销商
     * 
     * @param ids 需要删除的经销商主键
     * @return 结果
     */
    @Override
    public int deleteXrayDistributorsByIds(String ids)
    {
        return xrayDistributorsMapper.deleteXrayDistributorsByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除经销商信息
     * 
     * @param id 经销商主键
     * @return 结果
     */
    @Override
    public int deleteXrayDistributorsById(String id)
    {
        return xrayDistributorsMapper.deleteXrayDistributorsById(id);
    }

    @Override
    public XrayDistributors.DistributorStatsVO getStats() {
        return xrayDistributorsMapper.getDistributorStats();
    }

    @Override
    public List<XrayDistributors.LevelCountVO> getLevelCounts() {
        return xrayDistributorsMapper.getLevelCounts();
    }
    @Override
    public List<XrayDistributors.RegionTrendVO> getRegionTrends(int months) {
        return xrayDistributorsMapper.getRegionTrends(months);
    }

    @Override
    public List<XrayDistributorsConfig> selectXrayDistributorsConfigList(XrayDistributorsConfig xrayDistributorsConfig){
        return xrayDistributorsConfigMapper.selectXrayDistributorsConfigList(xrayDistributorsConfig);
    }

    @Override
    public void saveSettings(Long id, Long commissionRate, Long firstChargeBonus){
        XrayDistributorsConfig update = new XrayDistributorsConfig();
        update.setId(id);
        update.setCommissionRate(commissionRate);
        update.setFirstChargeBonus(firstChargeBonus);
        xrayDistributorsConfigMapper.updateXrayDistributorsConfig(update);
    }
}
