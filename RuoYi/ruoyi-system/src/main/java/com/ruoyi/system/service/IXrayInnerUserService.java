package com.ruoyi.system.service;


import com.ruoyi.system.domain.XrayInnerUser;

import java.util.List;

public interface IXrayInnerUserService {

    XrayInnerUser selectXrayInnerUserById(Long id);

    List<XrayInnerUser> selectXrayInnerUserLikeList(XrayInnerUser xrayInnerUser);

    public int insertXrayInnerUser(XrayInnerUser xrayInnerUser, String currentUser);

    public int updateXrayInnerUser(XrayInnerUser xrayInnerUser);

    public int deleteXrayInnerUserById(Long id);

    public int diableXrayInnerUserById(Long id);
}

