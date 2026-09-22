package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.XrayDistributors;
import com.ruoyi.system.domain.XrayPaymentOrder;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.service.IXrayDistributorsService;
import com.ruoyi.system.service.IXrayPaymentOrderService;
import com.ruoyi.system.service.IXrayUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账单管理 Controller
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/bill")
public class BillingController extends BaseController {

    @Autowired
    private IXrayPaymentOrderService paymentOrderService;

    @Autowired
    private IXrayUserService xrayUserService;

    @Autowired
    private IXrayDistributorsService distributorsService;

    /**
     * 账单页面入口（修复 404 Whitelabel 错误）
     */
    @RequiresPermissions("system:bill:view")
    @GetMapping()
    public String billing() {
        return "pages/billing";
    }

    /**
     * 分页查询账单列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(@RequestParam(value = "orderNo", required = false) String orderNo,
                              @RequestParam(value = "customerName", required = false) String customerName,
                              @RequestParam(value = "isFirstCharge", required = false) String isFirstCharge,
                              @RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate) {
        XrayPaymentOrder query = new XrayPaymentOrder();
        if (StringUtils.isNotBlank(orderNo)) {
            query.setOrderNo(orderNo.trim());
        }

        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(startDate)) {
            params.put("beginTime", startDate + " 00:00:00");
        }
        if (StringUtils.isNotBlank(endDate)) {
            params.put("endTime", endDate + " 23:59:59");
        }
        query.setParams(params);

        // 如果传了客户名，先查出对应用户ID
        List<Long> matchedUserIds = null;
        if (StringUtils.isNotBlank(customerName)) {
            XrayUser userQuery = new XrayUser();
            userQuery.setUsername(customerName.trim());
            List<XrayUser> users = xrayUserService.selectXrayUserList(userQuery);
            if (users.isEmpty()) {
                userQuery.setUsername(null);
                userQuery.setNick(customerName.trim());
                users = xrayUserService.selectXrayUserList(userQuery);
            }
            matchedUserIds = users.stream().map(XrayUser::getId).collect(Collectors.toList());
            if (matchedUserIds.isEmpty()) {
                return getDataTable(Collections.emptyList());
            }
            if (matchedUserIds.size() == 1) {
                query.setUserId(matchedUserIds.get(0));
            }
        }

        startPage();
        List<XrayPaymentOrder> orders = paymentOrderService.getOrders(query);
        if (orders.isEmpty()) {
            return getDataTable(Collections.emptyList());
        }

        // 收集需要查询的用户 ID
        Set<Long> userIds = orders.stream().map(XrayPaymentOrder::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, XrayUser> userMap = new HashMap<>();
        for (Long uid : userIds) {
            XrayUser u = xrayUserService.selectXrayUserById(uid);
            if (u != null) {
                userMap.put(uid, u);
            }
        }

        // 收集所有经销商用于匹配分润
        List<XrayDistributors> allDistributors = distributorsService.selectXrayDistributorsList(new XrayDistributors());
        Map<String, XrayDistributors> distMapByCode = allDistributors.stream()
                .filter(d -> StringUtils.isNotBlank(d.getBindingCode()))
                .collect(Collectors.toMap(XrayDistributors::getBindingCode, d -> d, (k1, k2) -> k1));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        List<Map<String, Object>> voList = new ArrayList<>();
        for (XrayPaymentOrder o : orders) {
            XrayUser u = userMap.get(o.getUserId());
            String uname = u != null ? (StringUtils.isNotBlank(u.getNick()) ? u.getNick() : u.getUsername()) : "未知用户 (" + o.getUserId() + ")";

            // 判断是否首充：累充月数<=1且是首单，或判断历史订单数
            boolean firstCharge = (u != null && (u.getCumulativeMonths() == null || u.getCumulativeMonths() <= 1));
            String firstChargeStr = firstCharge ? "是" : "否";

            // 首充筛选过滤
            if ("newUser".equalsIgnoreCase(isFirstCharge) && !firstCharge) {
                continue;
            }
            if ("conUser".equalsIgnoreCase(isFirstCharge) && firstCharge) {
                continue;
            }

            // 归属与分润
            boolean isDistributorUser = u != null && "outer".equalsIgnoreCase(u.getType());
            String belong = isDistributorUser ? "分销商" : "内部";
            String belongClass = isDistributorUser ? "belongto-outer" : "belongto-inner";

            BigDecimal commission = BigDecimal.ZERO;
            if (isDistributorUser && u.getInviteCode() != null && distMapByCode.containsKey(u.getInviteCode())) {
                XrayDistributors dist = distMapByCode.get(u.getInviteCode());
                if (dist.getCommissionRate() != null && dist.getCommissionRate() > 0 && o.getAmount() != null) {
                    commission = o.getAmount().multiply(new BigDecimal(dist.getCommissionRate()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                }
            }

            Map<String, Object> vo = new HashMap<>();
            vo.put("id", o.getId());
            vo.put("orderNo", o.getOrderNo());
            vo.put("paymentDate", o.getPaidAt() != null ? sdf.format(o.getPaidAt()) : (o.getCreatedAt() != null ? sdf.format(o.getCreatedAt()) : "-"));
            vo.put("paymentDateShort", o.getPaidAt() != null ? sdfDate.format(o.getPaidAt()) : (o.getCreatedAt() != null ? sdfDate.format(o.getCreatedAt()) : "-"));
            vo.put("customerName", uname);
            vo.put("amount", "$" + (o.getAmount() != null ? o.getAmount().setScale(2, RoundingMode.HALF_UP) : "0.00"));
            vo.put("rawAmount", o.getAmount() != null ? o.getAmount() : BigDecimal.ZERO);
            vo.put("currency", o.getCurrency() != null ? o.getCurrency() : "USD");
            vo.put("validPeriod", o.getExpiredAt() != null ? sdfDate.format(o.getExpiredAt()) : (u != null && u.getExpiration() != null ? sdfDate.format(u.getExpiration()) : "-"));
            vo.put("firstCharge", firstChargeStr);
            vo.put("isFirstChargeBool", firstCharge);
            vo.put("commission", "$" + commission.setScale(2, RoundingMode.HALF_UP));
            vo.put("belong", belong);
            vo.put("belongClass", belongClass);
            vo.put("status", o.getStatus());
            vo.put("type", o.getType());
            vo.put("tradeNo", o.getTradeNo());

            voList.add(vo);
        }

        return getDataTable(voList);
    }

    /**
     * 获取账单图表统计数据
     */
    @GetMapping("/stats")
    @ResponseBody
    public AjaxResult getStats(@RequestParam(value = "period", defaultValue = "month") String period) {
        XrayPaymentOrder query = new XrayPaymentOrder();
        List<XrayPaymentOrder> allOrders = paymentOrderService.getOrders(query);

        SimpleDateFormat fmt;
        if ("day".equalsIgnoreCase(period)) {
            fmt = new SimpleDateFormat("MM-dd");
        } else if ("year".equalsIgnoreCase(period)) {
            fmt = new SimpleDateFormat("yyyy");
        } else {
            fmt = new SimpleDateFormat("yyyy-MM");
        }

        Map<String, BigDecimal> trendMap = new TreeMap<>();
        BigDecimal firstChargeTotal = BigDecimal.ZERO;
        BigDecimal renewalTotal = BigDecimal.ZERO;

        for (XrayPaymentOrder o : allOrders) {
            BigDecimal amt = o.getAmount() != null ? o.getAmount() : BigDecimal.ZERO;
            Date d = o.getPaidAt() != null ? o.getPaidAt() : o.getCreatedAt();
            if (d != null) {
                String key = fmt.format(d);
                trendMap.put(key, trendMap.getOrDefault(key, BigDecimal.ZERO).add(amt));
            }

            // 简单统计首充/续费分布
            XrayUser u = o.getUserId() != null ? xrayUserService.selectXrayUserById(o.getUserId()) : null;
            if (u != null && (u.getCumulativeMonths() == null || u.getCumulativeMonths() <= 1)) {
                firstChargeTotal = firstChargeTotal.add(amt);
            } else {
                renewalTotal = renewalTotal.add(amt);
            }
        }

        BigDecimal total = firstChargeTotal.add(renewalTotal);
        int firstChargeRatio = total.compareTo(BigDecimal.ZERO) > 0 ?
                firstChargeTotal.multiply(new BigDecimal(100)).divide(total, 0, RoundingMode.HALF_UP).intValue() : 30;
        int renewalRatio = 100 - firstChargeRatio;

        Map<String, Object> data = new HashMap<>();
        data.put("trendCategories", new ArrayList<>(trendMap.keySet()));
        data.put("trendValues", trendMap.values().stream().map(b -> b.setScale(2, RoundingMode.HALF_UP)).collect(Collectors.toList()));
        data.put("firstChargeRatio", firstChargeRatio);
        data.put("renewalRatio", renewalRatio);
        data.put("firstChargeTotal", firstChargeTotal.setScale(2, RoundingMode.HALF_UP));
        data.put("renewalTotal", renewalTotal.setScale(2, RoundingMode.HALF_UP));
        data.put("totalAmount", total.setScale(2, RoundingMode.HALF_UP));

        return AjaxResult.success(data);
    }

    /**
     * 获取单笔账单详情
     */
    @GetMapping("/detail/{orderNo}")
    @ResponseBody
    public AjaxResult getDetail(@PathVariable String orderNo) {
        XrayPaymentOrder order = paymentOrderService.getOrderByOrderNo(orderNo);
        if (order == null) {
            return AjaxResult.error("账单不存在");
        }
        XrayUser user = xrayUserService.selectXrayUserById(order.getUserId());

        Map<String, Object> detail = new HashMap<>();
        detail.put("order", order);
        detail.put("username", user != null ? (StringUtils.isNotBlank(user.getNick()) ? user.getNick() : user.getUsername()) : "-");
        detail.put("email", user != null ? user.getEmail() : "-");
        detail.put("userType", user != null ? user.getType() : "-");
        return AjaxResult.success(detail);
    }
}
