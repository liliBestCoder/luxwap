from time import sleep

import websocket
import threading
from logger import logger

class WebSocketClient:
    def __init__(self, url, handler):
        self.url = url
        self.handler = handler
        self.ws = None

    def close(self):
        if self.ws:
            self.ws.close()

    def connect(self):
        self.ws = websocket.WebSocketApp(
            self.url,
            on_open=self.handler.on_open,
            on_message=self.handler.on_message,
            on_close=self.handler.on_close
        )
        self.ws_thread = threading.Thread(target=self.ws.run_forever, daemon=True)
        self.ws_thread.start()

    def reconnect(self):
        logger.info("[WebSocketClient] Reconnecting...")
        try:
            if self.ws:
                self.ws.close()
            if self.ws_thread and self.ws_thread.is_alive():
                self.ws_thread.join(timeout=5)

            self.connect()
        except:
            sleep(2)
            return self.reconnect()

        logger.info("[WebSocketClient] Reconnect Success.")
        return True

    def send(self, message):
        try:
            if self.ws:
                self.ws.send(message)
        except Exception as e:
            logger.error("[WebSocketClient] send msg error. message : s%", message, e)
