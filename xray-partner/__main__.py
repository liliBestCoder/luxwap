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

def main():
    client = WebSocketClient("ws://localhost:8080/ws", None)  # handler 先设为 None
    heartbeat_handler = HeartbeatHandler(client)
    status_handler = StatusHandler(client)
    command_handler = CommandHandler(client)
    traffic_collect_handler = TrafficCollectHandler(client)
    composite_handler = CompositeHandler([heartbeat_handler, status_handler, command_handler, traffic_collect_handler])
    #composite_handler = CompositeHandler([heartbeat_handler])
    client.handler = composite_handler
    client.connect()

    try:
        while True:
            time.sleep(5)
    except KeyboardInterrupt:
        print("退出中...")
        client.close()

    # api_client = XrayAPIClient()
    # stats = api_client.query_traffic(pattern="", reset=False)
    # result = [MessageToDict(stat) for stat in stats]
    # logger.info(result)
    #
    # cnt = api_client.query_user_count(tag="main", email="")
    # logger.info(cnt)



if __name__ == "__main__":
    main()
