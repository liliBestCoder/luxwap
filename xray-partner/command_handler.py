import json
from typing import List
from logger import logger
from config import config
from user_data import UserData
from concurrent.futures import ThreadPoolExecutor
from base_handler import BaseHandler
from xray_api_client import xray_api_client

class CommandHandler(BaseHandler):
    def __init__(self, client):
        self.client = client
        self.executor = ThreadPoolExecutor(max_workers=4)
    def on_message(self, message):
        try:
            data = json.loads(message)
            command = data["type"]
            data_obj = data.get("data")

            if command == "add_user" or command == "remove_user":
                msg_id = data.get("msg_id")
                user_data = UserData(**data_obj)
                def execute_user_op():
                    try:
                        if command == "remove_user":
                            xray_api_client.remove_user(config.XRAY_VLESS_INBOUND,
                                                        user_data.email)
                        else:
                            xray_api_client.add_vless_user(config.XRAY_VLESS_INBOUND,
                                                           user_data.user_id,
                                                           user_data.email,
                                                           user_data.flow or "xtls-rprx-vision",
                                                           user_data.encryption)
                    except Exception as ex:
                        logger.error("Error executing %s for %s: %s", command, user_data.email, ex)
                    finally:
                        if msg_id:
                            self.client.send(json.dumps({
                                "type": "ack",
                                "msg_id": msg_id
                            }))
                self.executor.submit(execute_user_op)
            elif command == "sync_users":
                batch_id = data.get("batch_id")
                seq = data.get("seq")
                user_data_list: List[UserData] = [UserData(**item) for item in (data_obj or [])]
                for user in user_data_list:
                    if user.op == "remove":
                        self.executor.submit(
                            xray_api_client.remove_user,
                            config.XRAY_VLESS_INBOUND,
                            user.email
                        )
                    elif user.op == "add":
                        self.executor.submit(
                            xray_api_client.add_vless_user,
                            config.XRAY_VLESS_INBOUND,
                            user.user_id,
                            user.email,
                            user.flow or "xtls-rprx-vision",
                            user.encryption
                        )
                if batch_id and seq is not None:
                    self.client.send(json.dumps({
                        "type": "sync_ack",
                        "batch_id": batch_id,
                        "seq": seq
                    }))
        except Exception as e:
            logger.error("CommandHandler Failed to handle message: %s", e)


