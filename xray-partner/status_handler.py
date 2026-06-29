import time
import json
import socket
import threading
import psutil
from config import config
from xray_api_client import xray_api_client
from base_handler import BaseHandler

class StatusHandler(BaseHandler):
    def __init__(self, client):
        self.monitor = -1
        self.client = client

    def on_open(self):
        if self.monitor == -1:
            self.monitor = self.start_status_monitor()
    def check_port(self, host, port, timeout=2):
        try:
            with socket.create_connection((host, port), timeout=timeout):
                return "up"
        except (socket.timeout, ConnectionRefusedError, OSError):
            return "down"

    def get_connections_count(self, port):
        # 获取所有的网络连接信息
        connections = psutil.net_connections(kind='inet')
        # 统计某个端口的连接数
        count = 0
        for conn in connections:
            # 判断连接是否是该端口的连接
            if conn.laddr.port == port:
                count += 1

        return count

    def start_status_monitor(self):
        # 重连之后也要同步一次用户
        self.client.send(json.dumps({"type": "sync_users"}))
        def monitor():
            while True:
                #xray重启需要同步一把用户
                user_cnt = xray_api_client.query_user_count(tag=config.XRAY_VLESS_INBOUND, email="")
                if user_cnt == 0:
                    self.client.send(json.dumps({"type": "sync_users"}))

                time.sleep(config.PARTNER_WS_HEALTH_CHECK_INTERVAL)
                status = self.check_port(config.XRAY_IP, int(config.XRAY_API_PORT))
                self.client.send(json.dumps({"type": "xray_status",
                                             "time": time.time(),
                                             "data": {
                                                "status": status
                                            }}))
        threading.Thread(target=monitor, daemon=True).start()
        return 1
