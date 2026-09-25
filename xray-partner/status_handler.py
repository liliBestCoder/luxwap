import time
import json
import socket
import threading
from config import config
from xray_api_client import xray_api_client
from base_handler import BaseHandler

class StatusHandler(BaseHandler):
    def __init__(self, client):
        self.monitor = -1
        self.client = client

    def on_open(self):
        # 每次连接或重连建立成功，立即同步一次全量用户
        try:
            self.client.send(json.dumps({"type": "sync_users"}))
        except Exception:
            pass

        if self.monitor == -1:
            self.monitor = self.start_status_monitor()

    def check_port(self, host, port, timeout=2):
        try:
            with socket.create_connection((host, port), timeout=timeout):
                return "up"
        except (socket.timeout, ConnectionRefusedError, OSError):
            return "down"

    def start_status_monitor(self):
        def monitor():
            last_sync_time = 0
            while True:
                # xray重启导致内存用户为0时拉取，增加 60 秒冷却时间防止空载高频风暴
                user_cnt = xray_api_client.query_user_count(tag=config.XRAY_VLESS_INBOUND, email="")
                now = time.time()
                cnt = getattr(user_cnt, 'count', user_cnt)
                if cnt == 0 and (now - last_sync_time > 60):
                    self.client.send(json.dumps({"type": "sync_users"}))
                    last_sync_time = now

                time.sleep(config.PARTNER_WS_HEALTH_CHECK_INTERVAL)
                port = int(config.XRAY_API_PORT) if config.XRAY_API_PORT else 10085
                status = self.check_port(config.XRAY_IP, port)
                self.client.send(json.dumps({"type": "xray_status",
                                             "time": time.time(),
                                             "data": {
                                                "status": status
                                            }}))
        threading.Thread(target=monitor, daemon=True).start()
        return 1
