from time import sleep

import websocket
import threading
from logger import logger

class WebSocketClient:
    def __init__(self, url, handler):
        self.url = url
        self.handler = handler
        self.ws = None
        self._send_lock = threading.Lock()
        self._reconnect_lock = threading.Lock()
        self._is_closing = False

    def close(self):
        self._is_closing = True
        if self.ws:
            try:
                self.ws.close()
            except Exception:
                pass

    def connect(self):
        self.ws = websocket.WebSocketApp(
            self.url,
            on_open=self.handler.on_open,
            on_message=self.handler.on_message,
            on_close=self.on_close
        )
        self.ws_thread = threading.Thread(target=self.ws.run_forever, daemon=True)
        self.ws_thread.start()

    def on_close(self, ws, close_status_code, close_msg):
        logger.warning("[WebSocketClient] Connection closed (code: %s, msg: %s)", close_status_code, close_msg)
        if self.handler:
            try:
                self.handler.on_close(ws, close_status_code, close_msg)
            except Exception as e:
                logger.error("[WebSocketClient] Error in handler on_close: %s", e)
        # 非主动关闭情况下，启动独立守护线程非阻塞秒级重连
        if not self._is_closing:
            threading.Thread(target=self.reconnect, daemon=True).start()

    def reconnect(self):
        if not self._reconnect_lock.acquire(blocking=False):
            logger.debug("[WebSocketClient] Reconnect already in progress, skipping duplicate.")
            return
        try:
            logger.info("[WebSocketClient] Reconnecting...")
            retry_delay = 2
            max_delay = 30
            while not self._is_closing:
                try:
                    if self.ws:
                        try:
                            self.ws.close()
                        except Exception:
                            pass
                    if self.ws_thread and self.ws_thread.is_alive() and self.ws_thread != threading.current_thread():
                        self.ws_thread.join(timeout=3)

                    self.connect()
                    logger.info("[WebSocketClient] Reconnect Success.")
                    return True
                except Exception as e:
                    logger.warning("[WebSocketClient] Reconnect failed: %s, retrying in %ss...", e, retry_delay)
                    sleep(retry_delay)
                    retry_delay = min(retry_delay * 2, max_delay)
        finally:
            self._reconnect_lock.release()

    def send(self, message):
        with self._send_lock:
            try:
                if self.ws:
                    self.ws.send(message)
            except Exception as e:
                msg_preview = (message[:120] + "...") if len(str(message)) > 120 else message
                logger.error("[WebSocketClient] send msg error. message : %s, error : %s", msg_preview, e)
