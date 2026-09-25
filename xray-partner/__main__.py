# __main__.py

import time
from websocket_client import WebSocketClient
from traffic_collect_handler import TrafficCollectHandler
from command_handler import CommandHandler
from composite_handler import CompositeHandler
from heartbeat_handler import HeartbeatHandler
from status_handler import StatusHandler
# from xray_api_client import XrayAPIClient
# from google.protobuf.json_format import MessageToDict
# from logger import logger

import sys
import traceback
from logger import logger

def run_agent():
    """业务 Worker 实例运行体"""
    from config import get_public_ip, config
    public_ip = get_public_ip()
    ws_url = config.PARTNER_WS_URL
    if public_ip:
        separator = "&" if "?" in ws_url else "?"
        ws_url = f"{ws_url}{separator}client_ip={public_ip}"
        logger.info("[Agent] 自动探测到本机公网IP: %s, 目标WS地址: %s", public_ip, ws_url)

    client = WebSocketClient(ws_url, None)  # handler 先设为 None
    heartbeat_handler = HeartbeatHandler(client)
    status_handler = StatusHandler(client)
    command_handler = CommandHandler(client)
    traffic_collect_handler = TrafficCollectHandler(client)
    composite_handler = CompositeHandler([heartbeat_handler, status_handler, command_handler, traffic_collect_handler])
    client.handler = composite_handler
    client.connect()

    try:
        while True:
            time.sleep(5)
    finally:
        client.close()

def main():
    """看门狗自愈监督器 (Watchdog Supervisor)"""
    logger.info("[Watchdog] xray-partner 守护引擎已启动")
    while True:
        try:
            run_agent()
        except KeyboardInterrupt:
            logger.info("[Watchdog] 收到中断信号 (KeyboardInterrupt)，正常退出。")
            break
        except SystemExit:
            logger.info("[Watchdog] 收到系统退出信号，正常退出。")
            break
        except BaseException as e:
            logger.error("[Watchdog] 检测到核心异常崩溃！原因: %s\n%s", e, traceback.format_exc())
            logger.info("[Watchdog] 节点将在 3 秒后自动拉起自愈重启...")
            time.sleep(3)

    # api_client = XrayAPIClient()
    # stats = api_client.query_traffic(pattern="", reset=False)
    # result = [MessageToDict(stat) for stat in stats]
    # logger.info(result)
    #
    # cnt = api_client.query_user_count(tag="main", email="")
    # logger.info(cnt)



if __name__ == "__main__":
    main()
