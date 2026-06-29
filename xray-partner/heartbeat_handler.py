import time
import json
import threading
from config import config
from logger import logger
from base_handler import BaseHandler

class HeartbeatHandler(BaseHandler):
    def __init__(self, client):
        self.monitor = -1
        self.client = client
        self.last_response_time = time.time()

    def on_open(self):
        if self.monitor == -1:
            self.monitor = self.start_conn_monitor()

    def on_message(self, message):
        # 不管什么消息都算“活跃”
        self.last_response_time = time.time()
        try:
            data = json.loads(message)
            if data.get("type") == "ping":
                self.client.send(json.dumps({"type": "pong"}))
        except Exception as e:
            logger.error("HeartbeatHandler Parse message error.", e)

    def is_alive(self, timeout=config.PARTNER_WS_HEALTH_CHECK_INTERVAL*6):
        return time.time() - self.last_response_time <= timeout

    def start_conn_monitor(self):
        def monitor():
            while True:
                time.sleep(config.PARTNER_WS_HEALTH_CHECK_INTERVAL)
                if not self.is_alive():
                    self.client.reconnect()
                    self.last_response_time = time.time()

        threading.Thread(target=monitor, daemon=True).start()
        return 1
