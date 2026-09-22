package com.ruoyi.web.controller.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.ConfigService;

import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.framework.security.CloudflareTurnstileService;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@Controller
public class SysLoginController extends BaseController
{
    /**
     * 是否开启记住我功能
     */
    @Value("${shiro.rememberMe.enabled: false}")
    private boolean rememberMe;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CloudflareTurnstileService turnstileService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, ModelMap mmap)
    {
        // 如果是Ajax请求，返回Json字符串。
        if (ServletUtils.isAjaxRequest(request))
        {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
        }
        // 是否开启记住我
        mmap.put("isRemembered", rememberMe);
        // 是否开启用户注册
        mmap.put("isAllowRegister", Convert.toBool(configService.getKey("sys.account.registerUser"), false));
        // Cloudflare Turnstile 特性开关与公钥传递
        mmap.put("turnstileEnabled", turnstileService != null && turnstileService.isEnabled());
        mmap.put("turnstileSiteKey", turnstileService != null ? turnstileService.getSiteKey() : "");
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public AjaxResult ajaxLogin(String username, String password, Boolean rememberMe,
                                @RequestParam(value = "cfTurnstileToken", required = false) String cfTurnstileToken,
                                HttpServletRequest request)
    {
        // 若开启了 Cloudflare Turnstile 人机验证，执行校验
        if (turnstileService != null && turnstileService.isEnabled())
        {
            String clientIp = ShiroUtils.getIp();
            if (!turnstileService.verify(cfTurnstileToken, clientIp))
            {
                return error("人机验证未通过，请刷新页面重试");
            }
        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        Subject subject = SecurityUtils.getSubject();
        try
        {
            subject.login(token);
            return success();
        }
        catch (AuthenticationException e)
        {
            String msg = "用户或密码错误";
            if (StringUtils.isNotEmpty(e.getMessage()))
            {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }

    @GetMapping("/unauth")
    public String unauth()
    {
        return "error/unauth";
    }
}
