"""
Luxwap 管理中台全交互流程与权限隔离 CDP 端到端自动化测试套件
基于 Chrome DevTools Protocol (Playwright 驱动)
"""

import time
import re
import pytest
from playwright.sync_api import expect
from conftest import login_as

class TestTC01LoginAuthentication:
    """TC-01: 登录与安全认证模块"""

    def test_tc01_01_empty_input_validation(self, page, base_url):
        page.goto(f"{base_url}/login")
        page.wait_for_selector("#loginBtn", timeout=10000)
        page.fill("#username", "")
        page.fill("#password", "")
        page.click("#loginBtn")
        
        error_msg = page.locator("#errorMsg")
        expect(error_msg).to_be_visible(timeout=5000)
        assert "用户名和密码不能为空" in (error_msg.text_content() or "")

    def test_tc01_02_wrong_password_interception(self, page, base_url):
        page.goto(f"{base_url}/login")
        page.wait_for_selector("#loginBtn", timeout=10000)
        page.fill("#username", "admin")
        page.fill("#password", "wrongpassword_test")
        page.click("#loginBtn")
        
        # 捕获错误提示浮层或文本
        page.wait_for_timeout(1000)
        error_msg = page.locator("#errorMsg")
        if error_msg.is_visible():
            text = error_msg.text_content() or ""
            assert any(k in text for k in ["错误", "不存在", "锁定", "失败"])

    def test_tc01_03_keyboard_enter_login(self, page, base_url):
        page.goto(f"{base_url}/login")
        page.wait_for_selector("#username", timeout=10000)
        page.fill("#username", "admin")
        page.fill("#password", "123456")
        page.keyboard.press("Enter")
        
        page.wait_for_url(f"{base_url}/index", timeout=10000)
        expect(page.locator(".sidebar")).to_be_visible()

    def test_tc01_04_cloudflare_disabled_pass(self, page, base_url):
        page.goto(f"{base_url}/login")
        # 特性开关关闭时，不应渲染任何阻碍自动化的 turnstile iframe
        turnstile_iframe = page.locator("iframe[src*='cloudflare']")
        assert turnstile_iframe.count() == 0


class TestTC02Navigation:
    """TC-02: 主控制台与侧边栏导航交互"""

    def test_tc02_01_customer_submenu_toggle(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        
        # 点击客户管理二级菜单
        cust_menu = page.locator("a:has-text('客户管理')")
        expect(cust_menu).to_be_visible()
        cust_menu.click()
        page.wait_for_timeout(500)
        
        submenu = page.locator("#customer-submenu")
        if submenu.count() > 0:
            expect(submenu).to_have_class(re.compile(r"active"))
            # 再次点击折叠
            cust_menu.click()
            page.wait_for_timeout(500)
            expect(submenu).not_to_have_class(re.compile(r"active"))

    def test_tc02_02_menu_active_state_switch(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        
        # 依次点击账单与线路信息
        bill_link = page.locator("a:has-text('账单')")
        bill_link.click()
        page.wait_for_timeout(500)
        expect(bill_link).to_have_class(re.compile(r"active"))
        
        line_link = page.locator("a:has-text('线路信息')")
        line_link.click()
        page.wait_for_timeout(500)
        expect(line_link).to_have_class(re.compile(r"active"))
        expect(bill_link).not_to_have_class(re.compile(r"active"))


class TestTC05BillingModule:
    """TC-05: 账单管理（针对新修复的 404、图表与订单详情）"""

    def test_tc05_01_billing_page_no_404(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        
        page.click("a:has-text('账单')")
        page.wait_for_timeout(1000)
        
        frame = page.frame_locator("#content-frame")
        # 必须正确渲染页面标题，且页面不包含 Whitelabel Error 404
        expect(frame.locator(".page-title")).to_have_text("账单管理")
        expect(frame.locator("body")).not_to_contain_text("Whitelabel Error Page")
        expect(frame.locator("body")).not_to_contain_text("404")

    def test_tc05_02_billing_trend_period_switch(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        page.click("a:has-text('账单')")
        page.wait_for_timeout(1000)
        
        frame = page.frame_locator("#content-frame")
        btn_month = frame.locator(".btn-period:has-text('按月')")
        btn_year = frame.locator(".btn-period:has-text('按年')")
        btn_day = frame.locator(".btn-period:has-text('按日')")
        
        # 切换周期并验证激活类
        btn_month.click()
        page.wait_for_timeout(300)
        expect(btn_month).to_have_class(re.compile(r"btn-primary"))
        
        btn_year.click()
        page.wait_for_timeout(300)
        expect(btn_year).to_have_class(re.compile(r"btn-primary"))
        expect(btn_month).to_have_class(re.compile(r"btn-secondary"))

    def test_tc05_03_billing_detail_modal(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        page.click("a:has-text('账单')")
        page.wait_for_timeout(1500)
        
        frame = page.frame_locator("#content-frame")
        detail_btn = frame.locator("#billsTableBody tr button:has-text('详情')").first
        if detail_btn.is_visible():
            detail_btn.click()
            modal = frame.locator("#billModal")
            expect(modal).to_be_visible()
            # 点击关闭按钮
            frame.locator("#billModal button:has-text('关闭')").click()
            expect(modal).to_be_hidden()


class TestTC06AdminSettings:
    """TC-06: 管理员配置（经销商政策赠送月数方案）"""

    def test_tc06_01_dealer_policy_bonus_months(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        page.click("a:has-text('管理员配置')")
        page.wait_for_timeout(1000)
        
        frame = page.frame_locator("#content-frame")
        # 点击经销商政策 Tab
        frame.locator("button.settings-tab:has-text('经销商政策')").click()
        page.wait_for_timeout(500)
        
        # 校验赠送月数输入控件
        bonus_input = frame.locator("#dealer-discount")
        expect(bonus_input).to_be_visible()
        bonus_input.fill("3")
        
        save_btn = frame.locator("button:has-text('保存经销商政策')")
        save_btn.click()
        page.wait_for_timeout(500)


class TestTC07SettingsPasswordModal:
    """TC-07: 用户设置与密码浮窗交互"""

    def test_tc07_01_password_modal_open_and_esc(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        page.click("a:has-text('设置')")
        page.wait_for_timeout(1000)
        
        frame = page.frame_locator("#content-frame")
        pwd_btn = frame.locator("button:has-text('修改密码')")
        pwd_btn.click()
        
        modal = frame.locator("#passwordModal")
        expect(modal).to_be_visible()
        
        # 按键盘 ESC 键测试关闭
        page.keyboard.press("Escape")
        expect(modal).to_be_hidden()

    def test_tc07_02_password_mismatch_interception(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        page.click("a:has-text('设置')")
        page.wait_for_timeout(1000)
        
        frame = page.frame_locator("#content-frame")
        frame.locator("button:has-text('修改密码')").click()
        
        modal = frame.locator("#passwordModal")
        expect(modal).to_be_visible()
        
        frame.locator("#current-password").fill("123456")
        frame.locator("#new-password").fill("password123")
        frame.locator("#confirm-password").fill("different456")
        frame.locator("button:has-text('更新密码')").click()
        
        # 模态框仍然打开，输入未被直接提交
        expect(modal).to_be_visible()


class TestTC08SessionLogout:
    """TC-08: 会话安全与注销退出"""

    def test_tc08_01_logout_flow(self, page, base_url):
        login_as(page, base_url, "admin", "123456")
        page.goto(f"{base_url}/logout")
        page.wait_for_url(f"{base_url}/login", timeout=8000)
        expect(page.locator("#loginBtn")).to_be_visible()


class TestTC09MultiRolePermissions:
    """TC-09: 5 大角色权限与菜单隔离自动化测试"""

    ROLE_MATRIX = [
        {
            "username": "admin",
            "name": "超级管理员",
            "visible": ["客户管理", "线路信息", "账单", "活动管理", "管理员配置"],
            "hidden": []
        },
        {
            "username": "manager",
            "name": "管理员",
            "visible": ["客户管理", "线路信息", "账单", "活动管理", "管理员配置"],
            "hidden": []
        },
        {
            "username": "support",
            "name": "客服专员",
            "visible": ["客户管理", "账单", "活动管理"],
            "hidden": ["线路信息", "管理员配置"]
        },
        {
            "username": "reseller",
            "name": "合作经销商",
            "visible": ["客户管理", "账单"],
            "hidden": ["线路信息", "活动管理", "管理员配置"]
        },
        {
            "username": "common",
            "name": "普通角色",
            "visible": ["线路信息", "活动管理"],
            "hidden": ["客户管理", "账单", "管理员配置"]
        }
    ]

    @pytest.mark.parametrize("case", ROLE_MATRIX)
    def test_role_menu_isolation(self, page, base_url, case):
        # 1. 登录该角色（统一密码 123456）
        login_as(page, base_url, case["username"], "123456")
        
        # 2. 校验权限内应该展示的菜单
        for menu_name in case["visible"]:
            locator = page.locator(f".sidebar a:has-text('{menu_name}')")
            expect(locator).to_be_visible()
            
        # 3. 校验受限隔离的菜单绝对不可见
        for menu_name in case["hidden"]:
            locator = page.locator(f".sidebar a:has-text('{menu_name}')")
            expect(locator).to_be_hidden()
            
        # 4. 退出登录清理上下文
        page.goto(f"{base_url}/logout")


class TestTC10ChainProxyApi:
    """TC-10: 链式代理上游端点存储、配置与删除全生命周期 API 自动化测试"""

    def test_tc10_01_unauthorized_rejection(self, page, base_url):
        # 1. 验证未携带有效 Token 访问直接被拦截
        endpoints = [
            "/api/client/chain-proxy/list",
            "/api/client/chain-proxy/config",
        ]
        for ep in endpoints:
            response = page.request.get(f"{base_url}{ep}")
            assert response.status in [401, 302, 200]
            if response.status == 200:
                json_data = response.json()
                assert json_data.get("code") in [401, 500, 301] or "token" in json_data.get("msg", "").lower()

    def test_tc10_02_authenticated_chain_proxy_lifecycle(self, page, base_url):
        # 客户端 API 使用 xray_user_session 中客户端用户的 Token (如 valid_test_token_2030)
        token_headers = {"token": "valid_test_token_2030", "Content-Type": "application/json"}
        
        # 2. 模拟客户端发送批量导入请求 (测试 proxyLines 字段)
        sample_payload = {
            "defaultProtocol": "socks5",
            "queryChannel": "iP2location",
            "proxyLines": "45.77.12.88:1080:proxyuser:testpass123{美国原生住宅}\n"
                         "http://149.28.33.12:8080{日本优质线路}\n"
                         "socks5://testuser:pwd456@104.238.150.2:10808[http://api.refresh.com/ip]{美西静态S5}"
        }
        import_res = page.request.post(
            f"{base_url}/api/client/chain-proxy/batch-import",
            data=sample_payload,
            headers=token_headers
        )
        assert import_res.status == 200
        data = import_res.json()
        assert data.get("code") in [0, 200]

        # 3. 查询上游端点列表
        list_res = page.request.get(f"{base_url}/api/client/chain-proxy/list", headers={"token": "valid_test_token_2030"})
        assert list_res.status == 200
        nodes_data = list_res.json()
        assert nodes_data.get("code") in [0, 200]
        nodes = nodes_data.get("data", [])
        
        # 导入的三条必须真实入库，否则后续断言会在空列表上空转
        imported_hosts = {n.get("host") for n in nodes}
        assert {"45.77.12.88", "149.28.33.12", "104.238.150.2"} <= imported_hosts,             f"批量导入的端点未全部入库，实际: {imported_hosts}"

        for node in nodes:
            # 校验非自增防遍历 Key
            assert node.get("nodeKey") is not None
            assert node.get("nodeKey").startswith("cp_")
            # 密码脱敏：有凭据的必须掩码，无凭据的应为空，且明文一律不得下发
            if node.get("username"):
                assert node.get("passwordMask") == "••••••",                     f"带凭据的端点必须脱敏展示: {node.get('host')}"
            else:
                assert not node.get("passwordMask"),                     f"无凭据的端点不应出现掩码: {node.get('host')}"
            assert "testpass123" not in str(node)
            assert "pwd456" not in str(node)

        target_node = next(n for n in nodes if n.get("host") == "45.77.12.88")
        if True:
            target_key = target_node.get("nodeKey")

            # 4. 测试客户端本地测绘结果上报同步接口
            report_payload = [
                {
                    "nodeKey": target_key,
                    "aliveStatus": 1,
                    "latencyMs": 85,
                    "exitIp": "45.77.12.88",
                    "countryCode": "US"
                }
            ]
            report_res = page.request.post(
                f"{base_url}/api/client/chain-proxy/report-status",
                data=report_payload,
                headers=token_headers
            )
            assert report_res.status == 200
            report_json = report_res.json()
            assert report_json.get("code") in [0, 200]

            # 5. 测试链式代理激活配置获取 (GET /api/client/chain-proxy/config)
            cfg_get_res = page.request.get(f"{base_url}/api/client/chain-proxy/config", headers={"token": "valid_test_token_2030"})
            assert cfg_get_res.status == 200
            assert cfg_get_res.json().get("code") in [0, 200]

            # 6. 测试链式代理激活配置更新 (POST /api/client/chain-proxy/config)
            cfg_post_payload = {
                "chainEnabled": True,
                "activeNodeKey": target_key,
                "mode": "FIXED_EXIT"
            }
            cfg_post_res = page.request.post(
                f"{base_url}/api/client/chain-proxy/config",
                data=cfg_post_payload,
                headers=token_headers
            )
            assert cfg_post_res.status == 200
            assert cfg_post_res.json().get("code") in [0, 200]

            # 7. 测试基于非自增 nodeKey 单条删除端点
            del_res = page.request.delete(f"{base_url}/api/client/chain-proxy/{target_key}", headers={"token": "valid_test_token_2030"})
            assert del_res.status == 200

            # 8. 测试批量删除 API (POST /api/client/chain-proxy/batch-delete)
            remaining_keys = [n.get("nodeKey") for n in nodes
                              if n.get("nodeKey") and n.get("nodeKey") != target_key
                              and n.get("host") in ("149.28.33.12", "104.238.150.2")]
            if remaining_keys:
                batch_del_res = page.request.post(
                    f"{base_url}/api/client/chain-proxy/batch-delete",
                    data=remaining_keys,
                    headers=token_headers
                )
                assert batch_del_res.status == 200
                assert batch_del_res.json().get("code") in [0, 200]

    def test_tc10_03_cross_tenant_isolation(self, page, base_url):
        """越权防护：B 用户不得读取、篡改或删除 A 用户的上游端点"""
        tenant_a = {"token": "valid_test_token_2030", "Content-Type": "application/json"}
        tenant_b = {"token": "valid_test_token_user2_2030", "Content-Type": "application/json"}

        # A 导入一个仅属于自己的端点
        marker_host = "198.51.100.77"
        page.request.post(
            f"{base_url}/api/client/chain-proxy/batch-import",
            data={"defaultProtocol": "socks5",
                  "proxyLines": f"{marker_host}:1080:tenant_a_user:tenant_a_secret{{A私有端点}}"},
            headers=tenant_a,
        )
        a_nodes = page.request.get(
            f"{base_url}/api/client/chain-proxy/list", headers=tenant_a
        ).json().get("data", [])
        a_target = next(n for n in a_nodes if n.get("host") == marker_host)
        a_key = a_target["nodeKey"]

        # 1. B 的列表中绝不能出现 A 的端点
        b_nodes = page.request.get(
            f"{base_url}/api/client/chain-proxy/list", headers=tenant_b
        ).json().get("data", [])
        b_hosts = {n.get("host") for n in b_nodes}
        assert marker_host not in b_hosts, "租户隔离失效：B 读取到了 A 的上游端点"
        assert a_key not in {n.get("nodeKey") for n in b_nodes}

        # 2. B 拿 A 的 nodeKey 上报状态，必须被静默忽略而非写入
        page.request.post(
            f"{base_url}/api/client/chain-proxy/report-status",
            data=[{"nodeKey": a_key, "aliveStatus": 2, "latencyMs": 9999,
                   "exitIp": "0.0.0.0", "countryCode": "XX"}],
            headers=tenant_b,
        )
        after_report = page.request.get(
            f"{base_url}/api/client/chain-proxy/list", headers=tenant_a
        ).json().get("data", [])
        a_after = next(n for n in after_report if n.get("nodeKey") == a_key)
        assert a_after.get("countryCode") != "XX", "租户隔离失效：B 篡改了 A 的端点状态"
        assert a_after.get("latencyMs") != 9999

        # 3. B 删除 A 的端点，必须删不掉
        page.request.delete(f"{base_url}/api/client/chain-proxy/{a_key}", headers=tenant_b)
        still_there = page.request.get(
            f"{base_url}/api/client/chain-proxy/list", headers=tenant_a
        ).json().get("data", [])
        assert a_key in {n.get("nodeKey") for n in still_there}, "租户隔离失效：B 删除了 A 的端点"

        # 4. B 批量删除同样无效
        page.request.post(
            f"{base_url}/api/client/chain-proxy/batch-delete",
            data=[a_key], headers=tenant_b,
        )
        final = page.request.get(
            f"{base_url}/api/client/chain-proxy/list", headers=tenant_a
        ).json().get("data", [])
        assert a_key in {n.get("nodeKey") for n in final}, "租户隔离失效：B 批量删除了 A 的端点"

        # 5. 两者的链式配置互不干扰
        page.request.post(
            f"{base_url}/api/client/chain-proxy/config",
            data={"chainEnabled": True, "activeNodeKey": a_key, "mode": "fixed_exit"},
            headers=tenant_a,
        )
        b_cfg = page.request.get(
            f"{base_url}/api/client/chain-proxy/config", headers=tenant_b
        ).json().get("data") or {}
        assert b_cfg.get("activeNodeKey") != a_key, "租户隔离失效：B 读到了 A 的激活配置"

        # 清理 A 的测试端点，保持用例可重复执行
        page.request.delete(f"{base_url}/api/client/chain-proxy/{a_key}", headers=tenant_a)


