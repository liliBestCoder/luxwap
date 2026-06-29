import grpc
from config import config
from logger import logger

from common.protocol import user_pb2
from common.serial import typed_message_pb2
from proxy.vless import account_pb2

from app.proxyman.command import command_pb2 as proxyman_command_pb2
from app.proxyman.command import command_pb2_grpc as proxyman_command_pb2_grpc

from app.stats.command import command_pb2 as stats_command_pb2
from app.stats.command import command_pb2_grpc as stats_command_pb2_grpc


class XrayAPIClient:
    def __init__(self):
        self.channel = grpc.insecure_channel(f"{config.XRAY_IP}:{config.XRAY_API_PORT}")
        self.stub = proxyman_command_pb2_grpc.HandlerServiceStub(self.channel)
        self.stats_stub = stats_command_pb2_grpc.StatsServiceStub(self.channel)
    def add_vless_user(self, tag: str, user_id: str, email: str, flow: str = "", encryption: str = ""):
        """
        添加 VLESS 协议的用户
        :param tag: inbound 的 tag，例如 "main"
        :param user_id: 用户的 UUID
        :param email: 用户的 email，用于标识
        :param flow: 可选 flow，例如 "xtls-rprx-vision"
        :param encryption: 可选加密字段，仅客户端使用（通常为 "none" 或 ""）
        :return: grpc.Response 对象
        """
        try:
            # 构造 VLESS Account 消息
            account = account_pb2.Account(
                id=user_id,
                flow=flow,
                encryption=encryption
            )

            account_typed_msg = typed_message_pb2.TypedMessage(
                type="xray.proxy.vless.Account",
                value=account.SerializeToString()
            )

            # 构造 protocol.User 消息
            proto_user = user_pb2.User(
                level=0,
                email=email,
                account=account_typed_msg
            )

            # 构造 AddUserOperation
            add_user_op = proxyman_command_pb2.AddUserOperation(user=proto_user)

            user_typed_msg = typed_message_pb2.TypedMessage(
                type="xray.app.proxyman.command.AddUserOperation",
                value=add_user_op.SerializeToString()
            )

            # 构造请求
            request = proxyman_command_pb2.AlterInboundRequest(
                tag=tag,
                operation=user_typed_msg
            )

            # 发送请求
            return self.stub.AlterInbound(request)
        except Exception as e:
            logger.warning("[add_vless_user] failed : tag : s%, user_id : s%, email : s%, error: s%", tag, user_id, email, e)

    def remove_user(self, tag: str, email: str):
        """
        从指定的 inbound 中移除一个用户。
        :param tag: inbound 的 tag，例如 "main"
        :param email: 要移除的用户的 email（必须与添加时一致）
        :return: grpc.Response 对象
        """
        # 构造 RemoveUserOperation
        try:
            remove_user_op = proxyman_command_pb2.RemoveUserOperation(email=email)

            user_typed_msg = typed_message_pb2.TypedMessage(
                type="xray.app.proxyman.command.RemoveUserOperation",
                value=remove_user_op.SerializeToString()
            )

            # 构造请求
            request = proxyman_command_pb2.AlterInboundRequest(
                tag=tag,
                operation=user_typed_msg
            )

            # 发送请求
            return self.stub.AlterInbound(request)
        except Exception as e:
            logger.warning("[remove_user] failed : tag : s%, email : s%, error: s%", tag, email, e)

    def query_traffic(self, pattern: str, reset: bool):
        """
        查询用户流量。
        :param stub: StatsServiceStub 实例
        :param pattern: 查询字符串，如 "user>>>test@xray.com>>>traffic>>>uplink"
        :param reset: 是否在查询后重置流量计数
        :return: 流量数据（单位：字节），查无结果时返回 -1
        """
        try:
            response = self.stats_stub.QueryStats(stats_command_pb2.QueryStatsRequest(
                pattern=pattern,
                reset=reset
            ))

            return response.stat
        except Exception as e:
            logger.warning("[query_traffic] failed , Error: s%", e)
            return None

    def query_user_count(self, tag: str, email: str):
        """
        查询用户数量。
        :param stub: StatsServiceStub 实例
        :return: 用户数量
        """
        try:
            get_inbound_user_request = proxyman_command_pb2.GetInboundUserRequest(tag =  tag, email = email)
            return self.stub.GetInboundUsersCount(get_inbound_user_request)
        except Exception as e:
            logger.warning("[query_user_count] failed , tag : s%, email : s%, Error : s%", tag, email, e)
            return -1

xray_api_client = XrayAPIClient()