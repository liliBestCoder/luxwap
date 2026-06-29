package com.ruoyi.system.controller;



import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;

import com.alibaba.fastjson.JSONObject;

import com.alipay.api.domain.AlipayTradePagePayModel;

import com.alipay.api.domain.AlipayTradePrecreateModel;

import com.alipay.api.internal.util.AlipaySignature;

import com.alipay.api.response.AlipayTradePrecreateResponse;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.ijpay.alipay.AliPayApi;

import com.ijpay.alipay.AliPayApiConfig;

import com.ijpay.alipay.AliPayApiConfigKit;

import com.ijpay.core.IJPayHttpResponse;

import com.ijpay.core.enums.SignType;

import com.ijpay.core.enums.TradeType;

import com.ijpay.core.kit.HttpKit;

import com.ijpay.core.kit.WxPayKit;

import com.ijpay.paypal.PayPalApi;

import com.ijpay.paypal.PayPalApiConfig;

import com.ijpay.paypal.PayPalApiConfigKit;

import com.ijpay.wxpay.WxPayApi;

import com.ijpay.wxpay.WxPayApiConfig;

import com.ijpay.wxpay.WxPayApiConfigKit;

import com.ijpay.wxpay.model.UnifiedOrderModel;

import com.ruoyi.common.core.controller.BaseController;

import com.ruoyi.common.core.domain.AjaxResult;

import com.ruoyi.common.threadlocal.XrayThreadLocal;

import com.ruoyi.system.domain.*;

import com.ruoyi.system.service.*;

import com.ruoyi.system.service.impl.OAuthServiceImpl;

import com.stripe.Stripe;

import com.stripe.exception.SignatureVerificationException;

import com.stripe.model.Event;

import com.stripe.model.PaymentIntent;

import com.stripe.model.PaymentMethod;

import com.stripe.net.Webhook;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;

import org.springframework.stereotype.Controller;

import org.springframework.stereotype.Service;

import org.springframework.util.Base64Utils;

import org.springframework.util.CollectionUtils;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;



import javax.crypto.Cipher;

import javax.crypto.Mac;

import javax.crypto.spec.GCMParameterSpec;

import javax.crypto.spec.SecretKeySpec;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import java.io.*;

import java.math.BigDecimal;

import java.net.HttpURLConnection;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.security.PublicKey;

import java.security.Signature;

import java.security.cert.CertificateFactory;

import java.security.cert.X509Certificate;

import java.time.LocalDateTime;

import java.time.ZoneId;

import java.util.*;

import java.util.stream.Collectors;



/**

 * Controller

 *

 * @author ruoyi

 * @date 2025-08-11

 */

@Controller

@RequestMapping("/api/client")

public class ClientApiController extends BaseController {



    @Autowired

    private IXrayUserService xrayUserService;

    @Autowired

    private IXrayActivityService xrayActivityService;

    @Autowired

    private IXrayDistributorsService xrayDistributorsService;

    @Autowired

    private EmailCodeService emailCodeService;

    @Autowired

    private IVpnLinesService vpnLinesService;



    @Autowired

    private OAuthServiceImpl oauthService;

    @Autowired

    private IXrayPacketService packetService;

    @Autowired

    private IXrayPaymentMerchantService merchantService;

    @Autowired

    private IXrayPaymentOrderService paymentOrderService;



    @PostMapping("/register")

    @ResponseBody

    public AjaxResult register(HttpServletRequest request, @RequestParam String username, @RequestParam String password, @RequestParam String deviceId,

                               @RequestParam String email, @RequestParam(required = false) String inviteCode, @RequestParam String verifyCode) {



        if (StringUtils.isBlank(username)) {

            return AjaxResult.error();

        }

        username = username.toLowerCase();



        if (StringUtils.isBlank(verifyCode)) {

            return AjaxResult.error("!");

        }



        String clientIp = getClientIp(request);

        if (!emailCodeService.verifyCode(username, verifyCode)) {

            return AjaxResult.error();

        }

        xrayUserService.register(username, password, email, inviteCode, clientIp, deviceId);

        return AjaxResult.success("!");

    }



    public String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isEmpty()) {

            //  IP ?

            if (ip.contains(",")) {

                ip = ip.split(",")[0].trim();

            }

            return ip;

        }



        ip = request.getHeader("X-Real-IP");

        if (ip != null && !ip.isEmpty()) {

            return ip;

        }



        // fallback

        return request.getRemoteAddr();

    }



    @PostMapping("/login")

    @ResponseBody

    public AjaxResult login(@RequestParam String username, @RequestParam String password,

                            @RequestParam String deviceId, @RequestParam String os,

                            @RequestParam String deviceType, @RequestParam String deviceName) throws Exception {

        if (StringUtils.isBlank(username)) {

            return AjaxResult.error();

        }

        username = username.toLowerCase();

        return AjaxResult.success(null, xrayUserService.login(username, password, deviceId, os, deviceType, deviceName));

    }



    public static class XrayUserResp {

        private String uuid;

        private String username;

        private String nick;

        private String country;

        private String password;

        private Long usedTraffic;

        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")

        private Date expiration;

        private Integer cumulativeMonths;

        private String type;



        private String verifyCode;



        public String getUuid() {

            return uuid;

        }



        public void setUuid(String uuid) {

            this.uuid = uuid;

        }



        public String getUsername() {

            return username;

        }



        public void setUsername(String username) {

            this.username = username;

        }



        public String getNick() {

            return nick;

        }



        public void setNick(String nick) {

            this.nick = nick;

        }



        public String getCountry() {

            return country;

        }



        public void setCountry(String country) {

            this.country = country;

        }



        public String getPassword() {

            return password;

        }



        public void setPassword(String password) {

            this.password = password;

        }



        public Long getUsedTraffic() {

            return usedTraffic;

        }



        public void setUsedTraffic(Long usedTraffic) {

            this.usedTraffic = usedTraffic;

        }



        public Date getExpiration() {

            return expiration;

        }



        public void setExpiration(Date expiration) {

            this.expiration = expiration;

        }



        public Integer getCumulativeMonths() {

            return cumulativeMonths;

        }



        public void setCumulativeMonths(Integer cumulativeMonths) {

            this.cumulativeMonths = cumulativeMonths;

        }



        public String getType() {

            return type;

        }



        public void setType(String type) {

            this.type = type;

        }



        public String getVerifyCode() {

            return verifyCode;

        }



        public void setVerifyCode(String verifyCode) {

            this.verifyCode = verifyCode;

        }

    }



    @GetMapping("/user-info")

    @ResponseBody

    public AjaxResult userInfo() {

        Long userId = XrayThreadLocal.getUid();

        XrayUser xrayUser = xrayUserService.selectXrayUserById(userId);

        XrayUserResp xrayUserResp = new XrayUserResp();

        BeanUtils.copyProperties(xrayUser, xrayUserResp);

        return AjaxResult.success(xrayUserResp);

    }



    @PostMapping("/update-user-info")

    @ResponseBody

    public AjaxResult updateUserInfo(XrayUserResp req) {

        Long userId = XrayThreadLocal.getUid();



        String verifyCode = req.getVerifyCode();

        if (StringUtils.isNotBlank(verifyCode)) {

            String username = req.getUsername();

            if (StringUtils.isBlank(username)) {

                return AjaxResult.error();

            }

            username = username.toLowerCase();



            XrayUser query = new XrayUser();

            query.setUsername(username);

            List<XrayUser> xrayUsers = xrayUserService.selectXrayUserList(query);

            if (!CollectionUtils.isEmpty(xrayUsers) && !xrayUsers.get(0).getId().equals(userId)) {

                return AjaxResult.error("!");

            }



            if (!emailCodeService.verifyCode(username, verifyCode)) {

                return AjaxResult.error();

            }

        }



        if (StringUtils.isNotBlank(req.getCountry())) {

            req.setCountry(req.getCountry().toUpperCase());

        }





        XrayUser update = new XrayUser();

        update.setId(userId);

        BeanUtils.copyProperties(req, update);

        xrayUserService.updateXrayUser(update);

        return AjaxResult.success();

    }



    // ?

    @GetMapping("/online-devices")

    @ResponseBody

    public AjaxResult getOnlineDevices() {

        Long userId = XrayThreadLocal.getUid();

        return AjaxResult.success(xrayUserService.getOnlineDevices(userId));

    }



    @PostMapping("/activity-join")

    @ResponseBody

    public AjaxResult joinActivity(@RequestParam String auditLink) {

        xrayActivityService.joinActivity(auditLink);

        return AjaxResult.success();

    }



    @GetMapping("/activity-rank-list")

    @ResponseBody

    public AjaxResult activityRankList() {

        return AjaxResult.success(xrayActivityService.activityRankList());

    }



    @PostMapping("/distributors-apply")

    @ResponseBody

    public AjaxResult apply(@RequestParam String email,

                            @RequestParam String contactPerson,

                            @RequestParam String level,

                            @RequestParam String paypal,

                            @RequestParam String region,

                            @RequestParam String exclusiveSuffix) {



        if (StringUtils.isBlank(email)) {

            return AjaxResult.error("!");

        }

        email = email.toLowerCase();



        XrayDistributors queryCondition = new XrayDistributors();

        queryCondition.setEmail(email);

        List<XrayDistributors> xrayDistributorsList = xrayDistributorsService.selectXrayDistributorsList(queryCondition);

        XrayDistributors exist = null;

        if (!CollectionUtils.isEmpty(xrayDistributorsList)) {

            XrayDistributors xrayDistributors = xrayDistributorsList.get(0);

            String status = xrayDistributors.getStatus();

            if ("approved".equals(status)) {

                return AjaxResult.error("!");

            } else if ("init".equals(status) || "rejected".equals(status)) {

                exist = xrayDistributors;

            }

        }



        XrayDistributors xrayDistributors = new XrayDistributors();

        xrayDistributors.setEmail(email);

        xrayDistributors.setContactPerson(contactPerson);

        xrayDistributors.setLevel(level);

        xrayDistributors.setPaypalAccount(paypal);

        xrayDistributors.setRegion(region);

        xrayDistributors.setExclusiveSuffix(exclusiveSuffix);

        xrayDistributors.setStatus("init"); // ?



        if (exist != null) {

            xrayDistributors.setId(exist.getId());

            xrayDistributors.setMonthlySales(exist.getMonthlySales());

            xrayDistributors.setCommissionRate(exist.getCommissionRate());

            xrayDistributors.setFirstChargeBonus(exist.getFirstChargeBonus());

            xrayDistributors.setBindingCode(exist.getBindingCode());



            return toAjax(xrayDistributorsService.updateXrayDistributors(xrayDistributors, null));

        } else {

            List<XrayDistributorsConfig> xrayDistributorsConfigs = xrayDistributorsService.selectXrayDistributorsConfigList(new XrayDistributorsConfig());

            XrayDistributorsConfig xrayDistributorsConfig = xrayDistributorsConfigs.get(0);

            xrayDistributors.setMonthlySales(new BigDecimal("0"));

            xrayDistributors.setCommissionRate(xrayDistributorsConfig.getCommissionRate());

            xrayDistributors.setFirstChargeBonus(xrayDistributorsConfig.getFirstChargeBonus());

            xrayDistributors.setBindingCode(XrayDistributorsController.BindingCodeGenerator.generateUniqueBindingCode());

            return toAjax(xrayDistributorsService.insertXrayDistributors(xrayDistributors));

        }

    }



    @PostMapping("/send-code")

    @ResponseBody

    public AjaxResult sendCode(@RequestParam String email) {

        if (StringUtils.isBlank(email)) {

            return AjaxResult.error("!");

        }

        email = email.toLowerCase();

        emailCodeService.sendCode(email);

        return AjaxResult.success();
    }



    @PostMapping("/change-password")

    @ResponseBody

    public AjaxResult changePassword(@RequestParam String oldPassword,

                                     @RequestParam String newPassword) {

        try {

            return AjaxResult.success(null, xrayUserService.changePassword(XrayThreadLocal.getUid(), oldPassword, newPassword));

        } catch (RuntimeException e) {

            return AjaxResult.error(e.getMessage());

        }

    }



    @PostMapping("/reset-password")

    @ResponseBody

    public AjaxResult resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String verifyCode) {

        try {

            if (StringUtils.isBlank(email)) {

                return AjaxResult.error("!");

            }

            email = email.toLowerCase();

            xrayUserService.resetPassword(email, newPassword, verifyCode);

            return AjaxResult.success();

        } catch (RuntimeException e) {

            return AjaxResult.error(e.getMessage());

        }

    }



    @GetMapping("/line-list")

    @ResponseBody

    public AjaxResult lineList() {

        try {

            return AjaxResult.success(vpnLinesService.generateVlessLinkList());

        } catch (RuntimeException e) {

            return AjaxResult.error(e.getMessage());

        }

    }



    @PostMapping({"/oauth/taskCreate", "/task/create"})

    @ResponseBody

    public AjaxResult createTask(HttpServletRequest request, OAuthTaskRequest oAuthTaskRequest) {

         String clientIp = getClientIp( request);

        return AjaxResult.success(oauthService.createLoginTask(clientIp, oAuthTaskRequest));

    }



    @GetMapping({"/oauth/taskCallback", "/oauth/callback"})

    @ResponseBody

    public AjaxResult callback(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws Exception {

        oauthService.handleCallback(code, state);

        return AjaxResult.success();

    }



    @GetMapping({"/oauth/taskResult", "/task/result"})

    @ResponseBody

    public AjaxResult getTaskResult(@RequestParam String taskId) {

        return AjaxResult.success(oauthService.getTaskResult(taskId));

    }



    @GetMapping("/payment/packetList")

    @ResponseBody

    public List<XrayPacket> getPacketList() {

        return packetService.getEnabledPacketList();

    }



    public static class PaymentMethodResp {

        private String name;

        private String type;



        public PaymentMethodResp() {

        }



        public PaymentMethodResp(String name, String type) {

            this.name = name;

            this.type = type;

        }



        public String getName() {

            return name;

        }



        public void setName(String name) {

            this.name = name;

        }



        public String getType() {

            return type;

        }



        public void setType(String type) {

            this.type = type;

        }

    }



    public static class PaymentConfigResp {

        private List<XrayPacket> packets;

        private List<PaymentMethodResp> paymentMethods;



        public List<XrayPacket> getPackets() {

            return packets;

        }



        public void setPackets(List<XrayPacket> packets) {

            this.packets = packets;

        }



        public List<PaymentMethodResp> getPaymentMethods() {

            return paymentMethods;

        }



        public void setPaymentMethods(List<PaymentMethodResp> paymentMethods) {

            this.paymentMethods = paymentMethods;

        }

    }



    private static class PayCreateResult {

        private String paymentForm;

        private String paymentUrl;

        private String qrCode;



        public String getPaymentForm() {

            return paymentForm;

        }



        public void setPaymentForm(String paymentForm) {

            this.paymentForm = paymentForm;

        }



        public String getPaymentUrl() {

            return paymentUrl;

        }



        public void setPaymentUrl(String paymentUrl) {

            this.paymentUrl = paymentUrl;

        }



        public String getQrCode() {

            return qrCode;

        }



        public void setQrCode(String qrCode) {

            this.qrCode = qrCode;

        }

    }



    @GetMapping("/payment/config")

    @ResponseBody

    public AjaxResult paymentConfig() {

        PaymentConfigResp resp = new PaymentConfigResp();

        resp.setPackets(packetService.getEnabledPacketList());

        resp.setPaymentMethods(merchantService.selectEnabled().stream()

                .map(merchant -> new PaymentMethodResp(merchant.getName(), merchant.getType()))

                .collect(Collectors.toList()));

        return AjaxResult.success(resp);

    }



    @GetMapping("/payment/orders")

    @ResponseBody

    public AjaxResult paymentOrders() {

        Long userId = XrayThreadLocal.getUid();

        XrayPaymentOrder query = new XrayPaymentOrder();

        query.setUserId(userId);

        List<Map<String, Object>> rows = paymentOrderService.getOrders(query).stream()

                .map(order -> {

                    XrayPacket packet = order.getPacketId() == null ? null : packetService.selectPacketById(order.getPacketId());

                    Map<String, Object> row = new LinkedHashMap<>();

                    row.put("orderNo", order.getOrderNo());

                    row.put("type", order.getType());

                    row.put("packetId", order.getPacketId());

                    row.put("packetName", packet == null ? "-" : packet.getName());

                    row.put("durationMonths", packet == null ? null : packet.getDurationMonths());

                    row.put("bonusMonths", packet == null ? null : packet.getBonusMonths());

                    row.put("amount", order.getAmount());

                    row.put("currency", order.getCurrency());

                    row.put("status", order.getStatus());

                    row.put("tradeNo", order.getTradeNo());

                    row.put("paidAmount", order.getPaidAmount());

                    row.put("paidCurrency", order.getPaidCurrency());

                    row.put("createdAt", formatDateTime(order.getCreatedAt()));

                    row.put("paidAt", formatDateTime(order.getPaidAt()));

                    row.put("successAt", formatDateTime(order.getSuccessAt()));

                    row.put("expiredAt", formatDateTime(order.getExpiredAt()));

                    return row;

                })

                .collect(Collectors.toList());

        return AjaxResult.success(rows);

    }



    @PostMapping("/payment/createOrder")

    @ResponseBody

    public AjaxResult createOrder(@RequestParam String type, @RequestParam Long packetId, @RequestParam(required = false) String submitToken) {

        Long userId = XrayThreadLocal.getUid();

        if (StringUtils.isBlank(submitToken)) {

            return AjaxResult.error("提交令牌不能为空");
        }

        XrayPaymentMerchant merchant = merchantService.selectByType(type);
        if (merchant == null || merchant.getStatus() == null || merchant.getStatus() != 1) {
            return AjaxResult.error("支付方式未启用");
        }

        XrayPacket packet = packetService.selectPacketById(packetId);
        if (packet == null || packet.getStatus() == null || packet.getStatus() != 1) {

            return AjaxResult.error("套餐不存在或已停用");
        }

        JSONObject config = JSON.parseObject(merchant.getConfig());

        String currency = "USD";

        XrayPaymentOrder reusableQuery = new XrayPaymentOrder();

        reusableQuery.setUserId(userId);

        reusableQuery.setPacketId(packetId);

        reusableQuery.setType(type);

        reusableQuery.setAmount(packet.getPrice());

        reusableQuery.setCurrency(currency);

        XrayPaymentOrder reusableOrder = paymentOrderService.getReusablePendingOrder(reusableQuery);

        if (false && canReusePaymentOrder(reusableOrder, config)) {

return AjaxResult.success()
                    .put("orderNo", reusableOrder.getOrderNo())

                    .put("status", reusableOrder.getStatus())

                    .put("paymentForm", reusableOrder.getPaymentUrl() != null && reusableOrder.getPaymentUrl().contains("<form") ? reusableOrder.getPaymentUrl() : null)

                    .put("qrCode", reusableOrder.getPaymentUrl() != null && !reusableOrder.getPaymentUrl().contains("<form") ? reusableOrder.getPaymentUrl() : null)

                    .put("paymentUrl", null);

        }



        String orderNo = generateOrderNo();

        Date now = new Date();

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);



        XrayPaymentOrder order = new XrayPaymentOrder();

        order.setUserId(userId);

        order.setOrderNo(orderNo);

        order.setPacketId(packetId);

        order.setAmount(packet.getPrice());

        order.setCurrency(currency);

        order.setType(type);

        order.setStatus("PENDING");

        order.setExpiredAt(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()));

        order.setCreatedAt(now);

        order.setUpdatedAt(now);

        order.setNotifyUrl(config.getString("notifyUrl"));

        order.setReturnUrl(config.getString("returnUrl"));

        try {

            paymentOrderService.createOrderWithSubmitToken(order, submitToken);

        } catch (IllegalStateException e) {

            return AjaxResult.error();

        }



        PayCreateResult payCreateResult = new PayCreateResult();

        if ("alipay".equalsIgnoreCase(type)) {

            try {

                payCreateResult = createAlipayOrder(config, order, packet);

            } catch (Exception e) {

                paymentOrderService.updateOrderStatus(orderNo, "FAILED");

                logger.error("Create alipay order failed, orderNo={}", orderNo, e);

                return AjaxResult.error("" + e.getMessage());

            }

        }



return AjaxResult.success()
                .put("orderNo", orderNo)
                .put("status", order.getStatus())
                .put("paymentForm", payCreateResult.getPaymentForm())

                .put("paymentUrl", payCreateResult.getPaymentUrl())

                .put("qrCode", payCreateResult.getQrCode());

    }



    private boolean canReusePaymentOrder(XrayPaymentOrder order, JSONObject config) {

        if (order == null || StringUtils.isBlank(order.getPaymentUrl())) {

            return false;

        }

        String notifyUrl = config.getString("notifyUrl");

        String returnUrl = config.getString("returnUrl");

        String serviceUrl = StringUtils.defaultIfBlank(config.getString("serviceUrl"), "https://openapi.alipay.com/gateway.do");

        String appId = config.getString("appId");

        return StringUtils.equals(order.getNotifyUrl(), notifyUrl)

                && StringUtils.equals(order.getReturnUrl(), returnUrl)

                && ((StringUtils.contains(order.getPaymentUrl(), serviceUrl) && StringUtils.contains(order.getPaymentUrl(), appId))

                    || !StringUtils.contains(order.getPaymentUrl(), "<form"));

    }



    @GetMapping("/payment/orderStatusBySubmitToken")
    @ResponseBody
    public AjaxResult orderStatusBySubmitToken(@RequestParam String submitToken) {
        Long userId = XrayThreadLocal.getUid();
        XrayPaymentOrder order = paymentOrderService.getOrderBySubmitToken(submitToken);
        if (order == null || !Objects.equals(order.getUserId(), userId)) {
            return AjaxResult.error("");
        }
        java.util.Map<String, Object> orderData = new java.util.LinkedHashMap<>();
        orderData.put("orderNo", order.getOrderNo());
        orderData.put("status", order.getStatus());
        orderData.put("tradeNo", order.getTradeNo());
        orderData.put("paidAmount", order.getPaidAmount());
        orderData.put("paidCurrency", order.getPaidCurrency());
        return AjaxResult.success(orderData);
    }

    @GetMapping("/payment/orderStatus")
    @ResponseBody
    public AjaxResult orderStatus(@RequestParam String orderNo) {

        Long userId = XrayThreadLocal.getUid();

        XrayPaymentOrder order = paymentOrderService.getOrderByOrderNo(orderNo);

        if (order == null || !Objects.equals(order.getUserId(), userId)) {

            return AjaxResult.error();

        }

        return AjaxResult.success()

                .put("orderNo", order.getOrderNo())

                .put("status", order.getStatus())

                .put("tradeNo", order.getTradeNo())

                .put("paidAmount", order.getPaidAmount())

                .put("paidCurrency", order.getPaidCurrency());

    }



    public String generateOrderNo() {

        return "ORDER_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);

    }



    private String formatDateTime(Date date) {

        if (date == null) {

            return null;

        }

        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

    }



    private PayCreateResult createAlipayOrder(JSONObject cfg, XrayPaymentOrder order, XrayPacket packet) throws Exception {

        String charset = StringUtils.defaultIfBlank(cfg.getString("charset"), "UTF-8");

        String signType = StringUtils.defaultIfBlank(cfg.getString("signType"), "RSA2");

        String serviceUrl = StringUtils.defaultIfBlank(cfg.getString("serviceUrl"), "https://openapi.alipay.com/gateway.do");



        AliPayApiConfig aliConfig = AliPayApiConfig.builder()

                .setAppId(cfg.getString("appId"))

                .setPrivateKey(cfg.getString("privateKey"))

                .setAliPayPublicKey(cfg.getString("alipayPublicKey"))

                .setCharset(charset)

                .setSignType(signType)

                .setServiceUrl(serviceUrl)

                .build();



        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

        model.setOutTradeNo(order.getOrderNo());

        model.setTotalAmount(order.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

        model.setSubject("Luxwap " + packet.getName());

        model.setProductCode("QR_CODE_OFFLINE");

        model.setTimeoutExpress("10m");

        model.setQrCodeTimeoutExpress("10m");



        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliConfig);

        AlipayTradePrecreateResponse response = AliPayApi.tradePrecreatePayToResponse(model, cfg.getString("notifyUrl"));

        if (response == null || !response.isSuccess() || StringUtils.isBlank(response.getQrCode())) {

            String message = response == null ? "empty response" : StringUtils.defaultIfBlank(response.getSubMsg(), response.getMsg());

            throw new RuntimeException(message);

        }

        String qrCode = response.getQrCode();



        XrayPaymentOrder update = new XrayPaymentOrder();

        update.setOrderNo(order.getOrderNo());

        update.setPaymentUrl(qrCode);

        update.setNotifyUrl(cfg.getString("notifyUrl"));

        update.setReturnUrl(cfg.getString("returnUrl"));

        paymentOrderService.updateOrder(update);



        PayCreateResult result = new PayCreateResult();

        result.setQrCode(qrCode);

        return result;

    }



    @PostMapping("/payment/notify/alipay")

    @ResponseBody

    public String alipayNotify(HttpServletRequest request) {

        Map<String, String> params = new HashMap<>();

        request.getParameterMap().forEach((key, value) -> {

            if (value != null && value.length > 0) {

                params.put(key, value[0]);

            }

        });



        String orderNo = params.get("out_trade_no");

        XrayPaymentNotifyLog log = buildAlipayNotifyLog(params);

        if (StringUtils.isBlank(orderNo)) {

            log.setFailReason("missing out_trade_no");

            saveFailedNotifyLog(log);

            return "fail";

        }



        XrayPaymentOrder order = paymentOrderService.getOrderByOrderNo(orderNo);

        if (order == null || !"alipay".equalsIgnoreCase(order.getType())) {

            log.setFailReason("order not found or channel mismatch");

            saveFailedNotifyLog(log);

            return "fail";

        }

        log.setCurrency(order.getCurrency());



        XrayPaymentMerchant merchant = merchantService.selectByType("alipay");

        if (merchant == null || StringUtils.isBlank(merchant.getConfig())) {

            log.setFailReason("alipay merchant config missing");

            saveFailedNotifyLog(log);

            return "fail";

        }



        JSONObject cfg = JSON.parseObject(merchant.getConfig());

        String charset = StringUtils.defaultIfBlank(cfg.getString("charset"), "UTF-8");

        String signType = StringUtils.defaultIfBlank(cfg.getString("signType"), "RSA2");



        try {

            boolean signVerified = AlipaySignature.rsaCheckV1(

                    params,

                    cfg.getString("alipayPublicKey"),

                    charset,

                    signType

            );

            log.setSignVerified(signVerified ? 1 : 0);

            if (!signVerified) {

                logger.warn("Alipay notify signature check failed, orderNo={}", orderNo);

                log.setFailReason("signature check failed");

                saveFailedNotifyLog(log);

                return "fail";

            }



            String totalAmount = params.get("total_amount");

            BigDecimal paidAmount = StringUtils.isBlank(totalAmount) ? null : new BigDecimal(totalAmount);

            if (paidAmount == null || paidAmount.compareTo(order.getAmount()) != 0) {

                logger.warn("Alipay notify amount mismatch, orderNo={}, paid={}, expected={}", orderNo, totalAmount, order.getAmount());

                log.setFailReason("amount mismatch");

                saveFailedNotifyLog(log);

                return "fail";

            }



            String tradeStatus = params.get("trade_status");

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {

                paymentOrderService.markSuccessAndActivate(

                        order,

                        log,

                        params.get("trade_no"),

                        paidAmount,

                        order.getCurrency(),

                        parseAlipayGmtPayment(params.get("gmt_payment"))

                );

                return "success";

            }



            saveHandledNotifyLog(log);

            if ("TRADE_CLOSED".equals(tradeStatus)) {

                paymentOrderService.updateOrderStatus(orderNo, "CLOSED");

            }

            return "success";

        } catch (Exception e) {

            logger.error("Handle alipay notify failed, orderNo={}", orderNo, e);

            log.setFailReason(e.getMessage());

            saveFailedNotifyLog(log);

            return "fail";

        }

    }



    private XrayPaymentNotifyLog buildAlipayNotifyLog(Map<String, String> params) {

        XrayPaymentNotifyLog log = new XrayPaymentNotifyLog();

        log.setOrderNo(params.get("out_trade_no"));

        log.setTradeNo(params.get("trade_no"));

        log.setNotifyId(params.get("notify_id"));

        log.setChannel("alipay");

        log.setTradeStatus(params.get("trade_status"));

        log.setCurrency(null);

        try {

            log.setAmount(StringUtils.isBlank(params.get("total_amount")) ? null : new BigDecimal(params.get("total_amount")));

        } catch (Exception ignored) {

            log.setAmount(null);

        }

        log.setRawPayload(JSON.toJSONString(params));

        log.setSignVerified(0);

        log.setHandled(0);

        return log;

    }



    private Date parseAlipayGmtPayment(String gmtPayment) {

        if (StringUtils.isBlank(gmtPayment)) {

            return new Date();

        }

        try {

            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            format.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            return format.parse(gmtPayment);

        } catch (Exception e) {

            return new Date();

        }

    }



    private void saveFailedNotifyLog(XrayPaymentNotifyLog log) {

        try {

            log.setHandled(0);

            paymentOrderService.saveNotifyLog(log);

        } catch (Exception ignored) {

            logger.warn("Save failed notify log failed: {}", ignored.getMessage());

        }

    }



    private void saveHandledNotifyLog(XrayPaymentNotifyLog log) {

        try {

            log.setHandled(1);

            paymentOrderService.saveNotifyLog(log);

        } catch (Exception ignored) {

            logger.warn("Save handled notify log failed: {}", ignored.getMessage());

        }

    }





//    @PostMapping("/payment/createOrder")

//    @ResponseBody

//    public AjaxResult createOrder(HttpServletRequest request, @RequestParam String type,

//                                  @RequestParam Long packetId,

//                                  @RequestParam String cardName,

//                                  @RequestParam String cardNumber,

//                                  @RequestParam String cardExpiry,

//                                  @RequestParam String cardSecurityCode) {

//        // 

//        XrayPaymentMerchant merchant = merchantService.selectByType(type);

//        if (merchant == null) {

//            return AjaxResult.error() " + type);

//        }

//

//        // 

//        XrayPacket packet = packetService.selectPacketById(packetId);

//        if (packet == null) {

//            return AjaxResult.error("ID");

//        }

//

//        JSONObject config = JSON.parseObject(merchant.getConfig());

//

//        // 

//        String subject = packet.getName();  // 

//        String amount = packet.getPrice().toString();  // 

//

//        // ?

//        String orderNo = generateOrderNo();

//

//        LocalDateTime expirationTime = LocalDateTime.now().plusHours(1);

//

//        XrayPaymentOrder order = new XrayPaymentOrder();

//        order.setOrderNo(orderNo);

//        order.setPacketId(packetId);

//        order.setAmount(new BigDecimal(amount));

//        order.setType(type);  // 

//        order.setStatus("PENDING");  // ?

//        order.setNotifyUrl(config.getString("notifyUrl"));

//        order.setReturnUrl(config.getString("returnUrl"));

//        order.setExpiredAt(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()));

//

//        // 

//        paymentOrderService.createOrder(order);

//

//        String clientIp = getClientIp(request);

//        String paymentUrl = null;

//

//        try {

//            switch (type) {

//                case "alipay":

//                    paymentUrl =  createAliPayOrder(config, orderNo, subject, amount);

//                    break;

//                case "wechat":

//                    paymentUrl =  createWechatOrder(clientIp, config, orderNo, subject, amount);

//                    break;

//                case "paypal":

//                    paymentUrl = createPaypalOrder(config, orderNo, subject, amount, cardName, cardNumber, cardExpiry, cardSecurityCode);

//                    break;

//                case "stripe":

//                    paymentUrl = createStripeOrder(config, orderNo, subject, amount, cardNumber, cardExpiry, cardSecurityCode, order);

//                    break;

//                case "circle":

//                    paymentUrl = createCircleOrder(config, orderNo, subject, amount, cardNumber, cardSecurityCode);

//                    break;

//                default:

//                    return AjaxResult.error();

//            }

//            XrayPaymentOrder update = new XrayPaymentOrder();

//            update.setId(order.getId());

//            if(!"pending".equals(order.getStatus())){

//                update.setStatus(order.getStatus());

//            }

//            update.setPaymentUrl(paymentUrl);

//

//            paymentOrderService.updateOrder(update);

//            return AjaxResult.success("").put("paymentUrl", paymentUrl);

//        } catch (Exception e) {

//            XrayPaymentOrder update = new XrayPaymentOrder();

//            update.setId(order.getId());

//            update.setStatus("failed");

//            paymentOrderService.updateOrder(update);

//            logger.error("created order failad, order : {} ", orderNo,  e);

//            return AjaxResult.error(": " + e.getMessage());

//        }

//    }

//

//    public String generateOrderNo() {

//        // ?

//        return "ORDER_v1_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);

//    }

//

//    private String createAliPayOrder(JSONObject cfg, String tradeNo, String subject, String amount) throws Exception {

//        AliPayApiConfig aliConfig = AliPayApiConfig.builder()

//                .setAppId(cfg.getString("appId"))

//                .setPrivateKey(cfg.getString("privateKey"))

//                .setAliPayPublicKey(cfg.getString("alipayPublicKey"))

//                .setCharset(cfg.getString("charset"))

//                .setSignType(cfg.getString("signType"))

//                .build();

//

//        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

//        model.setOutTradeNo(tradeNo);  // ?

//        model.setTotalAmount(amount);  // 

//        model.setSubject(subject);     // 

//        model.setProductCode("FAST_INSTANT_TRADE_PAY");  // ?

//

//        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliConfig);

//

//        String payUrl = AliPayApi.tradePage("POST", model, cfg.getString("notifyUrl"), cfg.getString("returnUrl"));

//        logger.info("alipay pay response : {}, orderNo : {}", payUrl, tradeNo);

//        return payUrl;

//    }

//

//

//    public static class H5SceneInfo {

//        private H5 h5_info;

//

//        public H5 getH5Info() {

//            return h5_info;

//        }

//

//        public void setH5Info(H5 h5_info) {

//            this.h5_info = h5_info;

//        }

//

//        public static class H5 {

//            private String type;

//            private String app_name;

//            private String bundle_id;

//            private String package_name;

//            private String wap_url;

//            private String wap_name;

//

//            public String getType() {

//                return type;

//            }

//

//            public void setType(String type) {

//                this.type = type;

//            }

//

//            public String getApp_name() {

//                return app_name;

//            }

//

//            public void setApp_name(String app_name) {

//                this.app_name = app_name;

//            }

//

//            public String getBundle_id() {

//                return bundle_id;

//            }

//

//            public void setBundle_id(String bundle_id) {

//                this.bundle_id = bundle_id;

//            }

//

//            public String getPackage_name() {

//                return package_name;

//            }

//

//            public void setPackage_name(String package_name) {

//                this.package_name = package_name;

//            }

//

//            public String getWap_url() {

//                return wap_url;

//            }

//

//            public void setWap_url(String wap_url) {

//                this.wap_url = wap_url;

//            }

//

//            public String getWap_name() {

//                return wap_name;

//            }

//

//            public void setWap_name(String wap_name) {

//                this.wap_name = wap_name;

//            }

//        }

//    }

//

//    private String createWechatOrder(String clientIp, JSONObject cfg, String tradeNo, String subject, String amount) {

//        try {

//            // 

//            WxPayApiConfig wxPayApiConfig = WxPayApiConfig.builder()

//                    .appId(cfg.getString("appId"))

//                    .mchId(cfg.getString("mchId"))

//                    .partnerKey(cfg.getString("apiKey"))

//                    .domain("domain")

//                    .build();

//

//            // 

//            WxPayApiConfigKit.setThreadLocalWxPayApiConfig(wxPayApiConfig);

//

//            H5SceneInfo sceneInfo = new H5SceneInfo();

//

//            H5SceneInfo.H5 h5_info = new H5SceneInfo.H5();

//            h5_info.setType("Wap");

//            //--""--"??

//            h5_info.setWap_url("https://gitee.com/javen205/IJPay");

//            h5_info.setWap_name("IJPay VIP ?);

//            sceneInfo.setH5Info(h5_info);

//

//            Map<String, String> params = UnifiedOrderModel

//                    .builder()

//                    .appid(wxPayApiConfig.getAppId())

//                    .mch_id(wxPayApiConfig.getMchId())

//                    .nonce_str(WxPayKit.generateStr())

//                    .body(subject)

//                    .attach(subject)

//                    .out_trade_no(tradeNo)

//                    .total_fee(amount)

//                    .spbill_create_ip(clientIp)

//                    .notify_url(cfg.getString("notifyUrl"))

//                    .trade_type(TradeType.MWEB.getTradeType())

//                    .scene_info(JSON.toJSONString(sceneInfo))

//                    .build()

//                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);

//

//            String xmlResult = WxPayApi.pushOrder(false, params);

//

//            Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

//

//            logger.info("wxpay pay response : {}, orderNo : {}", result, tradeNo);

//

//            String return_code = result.get("return_code");

//            String return_msg = result.get("return_msg");

//            if (!WxPayKit.codeIsOk(return_code)) {

//                throw new RuntimeException(return_msg);

//            }

//            String result_code = result.get("result_code");

//            if (!WxPayKit.codeIsOk(result_code)) {

//                throw new RuntimeException(return_msg);

//            }

//            // return_code result_codeSUCCESS

////            String prepayId = result.get("prepay_id");

//            String webUrl = result.get("mweb_url");

//            return webUrl;

//        } catch (Exception e) {

//            throw new RuntimeException(e);

//        }

//

//    }

//

//    private String createPaypalOrder(JSONObject cfg, String tradeNo, String subject, String amount,

//                                     String cardName, String cardNumber, String cardExpiry, String cardSecurityCode) {

//        try {

//            PayPalApiConfig config = new PayPalApiConfig();

//            config.setClientId(cfg.getString("clientId"));

//            config.setSecret(cfg.getString("secret"));

//            config.setSandBox(cfg.getBoolean("sandBox"));

//            config.setDomain(cfg.getString("domain"));

//            PayPalApiConfigKit.setThreadLocalApiConfig(config);

//

//            Map<String, Object> dataMap = new HashMap<>();

//            dataMap.put("intent", "CAPTURE");

//

//            ArrayList<Map<String, Object>> list = new ArrayList<>();

//

//            Map<String, Object> amountMap = new HashMap<>();

//            amountMap.put("currency_code", "USD");

//            amountMap.put("value", amount);

//

//            Map<String, Object> itemMap = new HashMap<>();

//            itemMap.put("amount", amountMap);

//            itemMap.put("description", subject);

//

//            list.add(itemMap);

//            dataMap.put("purchase_units", list);

//

//            Map<String, String> card = new HashMap<>();

//            card.put("name", cardName);

//            card.put("number", cardNumber);

//            card.put("security_code", cardSecurityCode);

//            card.put("expiry", cardExpiry);

//

//            Map<String, String> experienceContext = new HashMap<>();

//            experienceContext.put("return_url", config.getDomain().concat("/paypal/return"));

//            Map<String, Map<String,String>> paymentSource = new HashMap<>();

//            paymentSource.put("experience_context",experienceContext);

//            paymentSource.put("card",card);

//

//			dataMap.put("payment_source", paymentSource);

//            dataMap.put("invoice_number", tradeNo);

//

//            String data = JSON.toJSONString(dataMap);

//            IJPayHttpResponse resData = PayPalApi.createOrder(config, data);

//            if (resData.getStatus() == 201) {

//                String resultStr = resData.getBody();

//                        logger.info("paypay pay response : {}, orderNo : {}", resultStr, tradeNo);

//                JSONObject jsonObject = JSON.parseObject(resultStr);

//                JSONArray links = jsonObject.getJSONArray("links");

//                for (int i = 0; i < links.size(); i++) {

//                    JSONObject item = links.getJSONObject(i);

//                    String rel = item.getString("rel");

//                    String href = item.getString("href");

//                    if ("approve".equalsIgnoreCase(rel)) {

//                        return href;

//                    }

//                }

//            }

//            return null;

//        } catch (Exception e) {

//            throw new RuntimeException(e);

//        }

//    }

//

//

//    private String createCircleOrder(JSONObject config, String tradeNo, String subject, String amount, String cardNumber, String cardCvv) throws Exception {

//        String apiKey = config.getString("apiKey");

//        String apiBase = "https://api-sandbox.circle.com/v1/payments";

//

//        // ?payload

//        JSONObject payload = new JSONObject();

//        payload.put("amount", new JSONObject()

//                .fluentPut("amount", amount)

//                .fluentPut("currency", "USD"));

//        payload.put("source", new JSONObject()

//                .fluentPut("id", cardNumber)  // ?cardId

//                .fluentPut("type", "card")

//                .fluentPut("cvv", cardCvv)); // ?CVV

//        payload.put("description", subject);

//        payload.put("metadata", new JSONObject()

//                .fluentPut("orderNo", tradeNo));

//        payload.put("verification", "cvv"); //  CVV 

//        payload.put("idempotencyKey", UUID.randomUUID().toString());

//

//        // ?

//        HttpURLConnection conn = (HttpURLConnection) new URL(apiBase).openConnection();

//        conn.setRequestMethod("POST");

//        conn.setRequestProperty("Content-Type", "application/json");

//        conn.setRequestProperty("Authorization", "Bearer " + apiKey);

//        conn.setDoOutput(true);

//        try (OutputStream os = conn.getOutputStream()) {

//            os.write(payload.toJSONString().getBytes(StandardCharsets.UTF_8));

//        }

//

//        // 

//        int code = conn.getResponseCode();

//        if (code != 200 && code != 201) {

//            throw new RuntimeException("Circle API ? " + code);

//        }

//

//        String response = new BufferedReader(new InputStreamReader(conn.getInputStream()))

//                .lines()

//                .collect(Collectors.joining("\n"));

//        JSONObject respJson = JSON.parseObject(response);

//        JSONObject data = respJson.getJSONObject("data");

//        logger.info("circle pay response : {}, orderNo : {}", respJson, tradeNo);

//

//        // ?

//        if (data == null) {

//            throw new RuntimeException("Circle API ?data ");

//        }

//        String status = data.getString("status");

//        if (!"COMPLETED".equals(status)) {

//            logger.warn("status={}", status);

//        }

//

//        //  redirect URL

//        String paymentUrl = null;

//        if (data.getJSONObject("redirect") != null) {

//            paymentUrl = data.getJSONObject("redirect").getString("url");

//        }

//

//        return paymentUrl; // ?null

//    }

//

//

//

//    private String createStripeOrder(JSONObject config, String tradeNo, String subject, String amount, String cardNumber, String cardExpiry, String cardSecurityCode, XrayPaymentOrder order) throws Exception {

//        String secretKey = config.getString("secretKey");

//        Stripe.apiKey = secretKey;

//

//        String[] parts = cardExpiry.split("/"); // : MM/YY

//        Map<String, Object> cardParams = new HashMap<>();

//        cardParams.put("number", cardNumber);

//        cardParams.put("exp_month", parts[0]);

//        cardParams.put("exp_year", "20" + parts[1]);

//        cardParams.put("cvc", cardSecurityCode);

//

//        Map<String, Object> paymentMethodParams = new HashMap<>();

//        paymentMethodParams.put("type", "card");

//        paymentMethodParams.put("card", cardParams);

//

//        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

//

//        Map<String, Object> paymentIntentParams = new HashMap<>();

//        paymentIntentParams.put("amount", new BigDecimal(amount).multiply(new BigDecimal("100")).intValue()); // cents

//        paymentIntentParams.put("currency", "usd");

//        paymentIntentParams.put("description", subject);

//        paymentIntentParams.put("payment_method", paymentMethod.getId());

//        paymentIntentParams.put("confirm", true);

//        paymentIntentParams.put("metadata", Map.of("orderNo", tradeNo));

//

//        PaymentIntent intent = PaymentIntent.create(paymentIntentParams);

//        String paymentUrl = intent.getNextAction() != null ? intent.getNextAction().getRedirectToUrl().getUrl() : null;

//        logger.info("stripe pay response : {}, orderNo : {}", paymentUrl, tradeNo);

//

//

//        if ("succeeded".equals(intent.getStatus())) {

//            // 

//            order.setStatus("success");

//        } else if ("requires_action".equals(intent.getStatus()) && intent.getNextAction() != null) {

//            // 

//            return intent.getNextAction() != null ? intent.getNextAction().getRedirectToUrl().getUrl() : null;

//        } else {

//           throw new RuntimeException("Stripe API ");

//        }

//        return intent.getNextAction() != null ? intent.getNextAction().getRedirectToUrl().getUrl() : null;

//    }

//

//    @PostMapping("/{type}")

//    public ResponseEntity<String> notify(@PathVariable String type, HttpServletRequest request) {

//        try {

//            switch (type.toLowerCase()) {

//                case "alipay":

//                    return ResponseEntity.ok(handleAlipayCallback(request));

//                case "wechat":

//                    return ResponseEntity.ok(handleWechatCallback(request));

//                case "paypal":

//                    return ResponseEntity.ok(handlePaypalCallback(request));

//                case "stripe":

//                    return ResponseEntity.ok(handleStripeCallback(request));

//                case "circle":

//                    return ResponseEntity.ok(handleCircleCallback(request));

//                default:

//                    return ResponseEntity.badRequest().body("Unsupported payment type: " + type);

//            }

//        } catch (Exception e) {

//            e.printStackTrace();

//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

//                    .body("Notify handle error: " + e.getMessage());

//        }

//    }

//

//

//    @Value("${alipay.alipay-public-key}")

//    private String alipayPublicKey;

//

//    private String handleAlipayCallback(HttpServletRequest request) {

//        Map<String, String> params = new HashMap<>();

//        request.getParameterMap().forEach((k, v) -> params.put(k, v[0]));

//

//        try {

//            boolean signVerified = AlipaySignature.rsaCheckV1(

//                    params,

//                    alipayPublicKey,

//                    "UTF-8",

//                    "RSA2"

//            );

//

//            if (!signVerified) {

//                return "fail";  // 

//            }

//

//            String orderNo = params.get("out_trade_no");

//            String tradeStatus = params.get("trade_status");

//

//            if ("TRADE_SUCCESS".equals(tradeStatus)) {

//                paymentOrderService.updateOrderStatus(orderNo, "SUCCESS");

//            } else if ("TRADE_CLOSED".equals(tradeStatus)) {

//                paymentOrderService.updateOrderStatus(orderNo, "CLOSED");

//            } else {

//                paymentOrderService.updateOrderStatus(orderNo, "FAILED");

//            }

//

//            return "success";  // ?"success"

//        } catch (Exception e) {

//            e.printStackTrace();

//            return "fail";

//        }

//    }

//

//    @Value("${wechatpay.api.v3key}")

//    private String apiV3Key; //  APIv3 ?2?

//

//    @Value("${wechatpay.platform.cert}")

//    private String platformCertPath; // 

//

//    private String handleWechatCallback(HttpServletRequest request) throws Exception {

//        //  Header

//        String timestamp = request.getHeader("Wechatpay-Timestamp");

//        String nonce = request.getHeader("Wechatpay-Nonce");

//        String signature = request.getHeader("Wechatpay-Signature");

//        String serialNo = request.getHeader("Wechatpay-Serial");

//

//        //  Body

//        String body = new BufferedReader(new InputStreamReader(request.getInputStream()))

//                .lines().collect(Collectors.joining("\n"));

//

//        // 1 

//        if (!verifyWechatSignature(timestamp, nonce, body, signature, serialNo)) {

//            return "{\"code\":\"FAIL\",\"message\":\"Invalid Signature\"}";

//        }

//

//        // 2 

//        JSONObject json = JSON.parseObject(body);

//        JSONObject resource = json.getJSONObject("resource");

//        String ciphertext = resource.getString("ciphertext");

//        String nonceStr = resource.getString("nonce");

//        String associatedData = resource.getString("associated_data");

//

//        String decryptData = decryptResource(apiV3Key, associatedData, nonceStr, ciphertext);

//        JSONObject data = JSON.parseObject(decryptData);

//

//        String orderNo = data.getString("out_trade_no");

//        String tradeState = data.getString("trade_state");

//

//        if ("SUCCESS".equals(tradeState)) {

//            paymentOrderService.updateOrderStatus(orderNo, "SUCCESS");

//        } else {

//            paymentOrderService.updateOrderStatus(orderNo, "FAILED");

//        }

//

//        return "{\"code\":\"SUCCESS\",\"message\":\"OK\"}";

//    }

//

//    private boolean verifyWechatSignature(String timestamp, String nonce, String body, String signature, String serialNo) throws Exception {

//        // 

//        CertificateFactory cf = CertificateFactory.getInstance("X509");

//        X509Certificate cert;

//        try (FileInputStream in = new FileInputStream(platformCertPath)) {

//            cert = (X509Certificate) cf.generateCertificate(in);

//        }

//

//        PublicKey publicKey = cert.getPublicKey();

//        String message = timestamp + "\n" + nonce + "\n" + body + "\n";

//

//        Signature sign = Signature.getInstance("SHA256withRSA");

//        sign.initVerify(publicKey);

//        sign.update(message.getBytes(StandardCharsets.UTF_8));

//

//        return sign.verify(Base64.getDecoder().decode(signature));

//    }

//

//    private String decryptResource(String apiV3Key, String associatedData, String nonce, String ciphertext) throws Exception {

//        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

//        SecretKeySpec key = new SecretKeySpec(apiV3Key.getBytes(StandardCharsets.UTF_8), "AES");

//        GCMParameterSpec spec = new GCMParameterSpec(128, nonce.getBytes(StandardCharsets.UTF_8));

//        cipher.init(Cipher.DECRYPT_MODE, key, spec);

//        cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));

//        byte[] decoded = Base64.getDecoder().decode(ciphertext);

//        byte[] result = cipher.doFinal(decoded);

//        return new String(result, StandardCharsets.UTF_8);

//    }

//

//    @Value("${paypal.mode}")

//    private String mode;

//

//    @Value("${paypal.webhook-id}")

//    private String webhookId;

//

//    private String handlePaypalCallback(HttpServletRequest request) throws IOException {

//        String body = new BufferedReader(new InputStreamReader(request.getInputStream()))

//                .lines().collect(Collectors.joining("\n"));

//

//        //  PayPal Header

//        Map<String, String> headers = new HashMap<>();

//        headers.put("transmission_id", request.getHeader("PAYPAL-TRANSMISSION-ID"));

//        headers.put("transmission_time", request.getHeader("PAYPAL-TRANSMISSION-TIME"));

//        headers.put("cert_url", request.getHeader("PAYPAL-CERT-URL"));

//        headers.put("auth_algo", request.getHeader("PAYPAL-AUTH-ALGO"));

//        headers.put("transmission_sig", request.getHeader("PAYPAL-TRANSMISSION-SIG"));

//

//        //  Webhook

//        if (!verifyWebhook(body, headers)) {

//            return "INVALID";

//        }

//

//        JSONObject event = JSON.parseObject(body);

//        JSONObject resource = event.getJSONObject("resource");

//        String orderNo = resource.getString("invoice_id");

//        String status = resource.getString("status");

//

//        if ("COMPLETED".equals(status)) {

//            paymentOrderService.updateOrderStatus(orderNo, "SUCCESS");

//        } else {

//            paymentOrderService.updateOrderStatus(orderNo, "FAILED");

//        }

//

//        return "OK";

//    }

//

//    @Service

//    public class PaypalUtil {

//

//        @Value("${paypal.client-id}")

//        private String clientId;

//

//        @Value("${paypal.client-secret}")

//        private String clientSecret;

//

//        @Value("${paypal.mode}")

//        private String mode;

//

//        public String getAccessToken() {

//            String url = (mode.equals("live") ? "https://api.paypal.com" : "https://api.sandbox.paypal.com") + "/v1/oauth2/token";

//            RestTemplate restTemplate = new RestTemplate();

//

//            HttpHeaders headers = new HttpHeaders();

//            String auth = clientId + ":" + clientSecret;

//            headers.set("Authorization", "Basic " + Base64Utils.encodeToString(auth.getBytes()));

//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//

//            HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

//

//            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

//            JSONObject json = JSON.parseObject(resp.getBody());

//            return json.getString("access_token");

//        }

//    }

//

//    @Autowired

//    private PaypalUtil paypalUtil;

//

//    private boolean verifyWebhook(String body, Map<String, String> headers) {

//        try {

//            String accessToken = paypalUtil.getAccessToken();

//            String url = (mode.equals("live") ? "https://api.paypal.com" : "https://api.sandbox.paypal.com") +

//                    "/v1/notifications/verify-webhook-signature";

//

//            JSONObject payload = new JSONObject();

//            payload.put("transmission_id", headers.get("transmission_id"));

//            payload.put("transmission_time", headers.get("transmission_time"));

//            payload.put("cert_url", headers.get("cert_url"));

//            payload.put("auth_algo", headers.get("auth_algo"));

//            payload.put("transmission_sig", headers.get("transmission_sig"));

//            payload.put("webhook_id", webhookId);

//            payload.put("webhook_event", JSON.parse(body));

//

//            HttpHeaders httpHeaders = new HttpHeaders();

//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

//            httpHeaders.setBearerAuth(accessToken);

//

//            HttpEntity<String> entity = new HttpEntity<>(payload.toJSONString(), httpHeaders);

//            RestTemplate restTemplate = new RestTemplate();

//            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

//

//            JSONObject json = JSON.parseObject(resp.getBody());

//            return "SUCCESS".equals(json.getString("verification_status"));

//        } catch (Exception e) {

//            e.printStackTrace();

//            return false;

//        }

//    }

//

//    @Value("${stripe.webhook.secret:}")

//    private String stripeSecret;

//    private String handleStripeCallback(HttpServletRequest request) throws IOException {

//        String payload = new BufferedReader(new InputStreamReader(request.getInputStream()))

//                .lines().collect(Collectors.joining("\n"));

//        String sigHeader = request.getHeader("Stripe-Signature");

//

//        try {

//            Event event = Webhook.constructEvent(payload, sigHeader, stripeSecret);

//            if ("payment_intent.succeeded".equals(event.getType())) {

//                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()

//                        .getObject().orElse(null);

//                if (intent != null) {

//                    String orderNo = intent.getMetadata().get("orderNo");

//                    paymentOrderService.updateOrderStatus(orderNo, "SUCCESS");

//                }

//            } else if ("payment_intent.payment_failed".equals(event.getType())) {

//                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()

//                        .getObject().orElse(null);

//                if (intent != null) {

//                    String orderNo = intent.getMetadata().get("orderNo");

//                    paymentOrderService.updateOrderStatus(orderNo, "FAILED");

//                }

//            }

//        } catch (SignatureVerificationException e) {

//            return "invalid signature";

//        }

//        return "";

//    }

//

//    @Value("${circle.webhook.secret:}")

//    private String circleSecret;

//    private String handleCircleCallback(HttpServletRequest request) throws IOException {

//        String signatureHeader = request.getHeader("Circle-Signature");

//        String timestampHeader = request.getHeader("Circle-Timestamp");

//

//        String body = new BufferedReader(new InputStreamReader(request.getInputStream()))

//                .lines().collect(Collectors.joining("\n"));

//

//        // ===  ===

//        if (!verifyCircleSignature(signatureHeader, timestampHeader, body, circleSecret)) {

//            return "Invalid signature";

//        }

//

//        JSONObject json = JSON.parseObject(body);

//        JSONObject data = json.getJSONObject("data");

//

//        String orderNo = data.getJSONObject("metadata").getString("orderNo");

//        String status = data.getString("status");

//

//        if ("paid".equalsIgnoreCase(status)) {

//            paymentOrderService.updateOrderStatus(orderNo, "SUCCESS");

//        } else if ("failed".equalsIgnoreCase(status)) {

//            paymentOrderService.updateOrderStatus(orderNo, "FAILED");

//        }

//        return "OK";

//    }

//

//    private boolean verifyCircleSignature(String signature, String timestamp, String body, String secret) {

//        try {

//            String message = timestamp + "." + body;

//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

//            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

//            sha256_HMAC.init(secret_key);

//            byte[] hash = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));

//            String computedSignature = Base64.getEncoder().encodeToString(hash);

//

//            return computedSignature.equals(signature);

//        } catch (Exception e) {

//            e.printStackTrace();

//            return false;

//        }

//    }





}

