package com.ruoyi.framework.filter;

import com.ruoyi.common.threadlocal.XrayThreadLocal;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.domain.XrayUserSession;
import com.ruoyi.system.mapper.XrayUserSessionMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 客户端 JWT 过滤器（独立于后台的 JwtFilter）
 */
public class ClientJwtFilter extends AccessControlFilter {

    private XrayUserSessionMapper xrayUserSessionMapper;

    public ClientJwtFilter(XrayUserSessionMapper xrayUserSessionMapper) {
        this.xrayUserSessionMapper = xrayUserSessionMapper;
    }

    /**
     * 访问前置处理
     * 返回 true = 允许访问；false = 拒绝访问并走 onAccessDenied
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false; // 强制走 onAccessDenied 做 token 校验
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getToken(httpRequest);

        if (StringUtils.isEmpty(token)) {
            // 没 token，直接拒绝
            sendUnauthorized(response, "无效的token");
            return false;
        }

        XrayUserSession queryCondition = new XrayUserSession();
        queryCondition.setToken(token);
        List<XrayUserSession> xrayUserSessionList = xrayUserSessionMapper.selectXrayUserSessionList(queryCondition);
        if(CollectionUtils.isEmpty(xrayUserSessionList)){
            sendUnauthorized(response, "无效的token");
            return false;
        }

        XrayUserSession xrayUserSession = xrayUserSessionList.get(0);
        boolean expired = xrayUserSession.getTokenExpiration().before(new Date());
        boolean cancel = xrayUserSession.getStatus() == 1;
        if (expired || cancel) {
            sendUnauthorized(response, "token已过期或无效");
            return false;
        }
        XrayThreadLocal.setUid(xrayUserSession.getUserId());
        return true; // 认证通过
    }

    /**
     * 从请求中获取 token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            token = request.getHeader("token");
        }
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        if (StringUtils.isEmpty(token) && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("client_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    /**
     * 输出 401 JSON 响应
     */
    private void sendUnauthorized(ServletResponse response, String msg) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.setContentType("application/json");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ServletUtils.renderString(httpResponse, "{\"code\":401,\"msg\":\"" + msg + "\"}");
    }
}

