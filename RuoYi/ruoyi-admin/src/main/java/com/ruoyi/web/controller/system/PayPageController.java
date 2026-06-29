package com.ruoyi.web.controller.system;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.common.threadlocal.XrayThreadLocal;
import com.ruoyi.system.domain.XrayPaymentSubmitToken;
import com.ruoyi.system.service.IXrayPaymentSubmitTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PayPageController
{
    private static final Pattern SUBMIT_TOKEN_PATTERN = Pattern.compile("^[0-9a-f]{32}$");
    private static final MediaType HTML_UTF8 = MediaType.valueOf("text/html;charset=UTF-8");

    @Autowired
    private IXrayPaymentSubmitTokenService submitTokenService;

    @GetMapping("/pay")
    public ResponseEntity<String> pay(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String token = request.getParameter("token");
        if (StringUtils.isNotBlank(token))
        {
            Cookie cookie = new Cookie("client_token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
        }

        String submitToken = request.getParameter("submitToken");
        if (StringUtils.isBlank(submitToken) || !SUBMIT_TOKEN_PATTERN.matcher(submitToken).matches()) {
            response.sendRedirect("/pay/error?msg=invalid_submit_token");
            return null;
        }

        XrayPaymentSubmitToken st = submitTokenService.getByToken(submitToken);
        if (st == null) {
            // 首次访问，插入并显示支付页
            Date expiredAt = Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());
            submitTokenService.createToken(submitToken, XrayThreadLocal.getUid(), expiredAt);
        } else if (st.getStatus() != 0) {
            // 已使用或已过期
            response.sendRedirect("/pay/error?msg=already_used");
            return null;
        }
        // status == 0：已存在但未使用，直接显示支付页（刷新场景）

        ClassPathResource resource = new ClassPathResource("static/pay.html");
        String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        html = html.replace("__PAY_SUBMIT_TOKEN__", submitToken);
        return ResponseEntity.ok()
                .contentType(HTML_UTF8)
                .body(html);
    }

    @GetMapping("/pay/error")
    public ResponseEntity<String> payError(HttpServletRequest request)
    {
        String msg = request.getParameter("msg");
        String text;
        if ("invalid_submit_token".equals(msg)) {
            text = "参数错误：无效的 submitToken，请回到客户端重新点击「续费」。";
        } else if ("already_used".equals(msg)) {
            text = "订单已经处理，不可重复支付。";
        } else {
            text = "请求出错，请重试。";
        }
        return ResponseEntity.badRequest()
                .contentType(HTML_UTF8)
                .body("<html><head><meta charset=\"utf-8\"></head><body><h3>错误</h3><p>" + text + "</p></body></html>");
    }
}
