from typing import Optional
import yaml
import json

class Config:
    XRAY_IP: Optional[str] = "127.0.0.1"
    XRAY_API_PORT: Optional[str] = "8080"
    PARTNER_WS_HEALTH_CHECK_INTERVAL: Optional[int] = 10
    PARTNER_TRAFFIC_COLLECT_INTERVAL: Optional[int] = 10
    PARTNER_TRAFFIC_REPORT_INTERVAL: Optional[int] = 60 * 2  # 10mins上报一次
    PARTNER_LOG_FILE_NAME: Optional[str] = "xray-partner.log"
    PARTNER_LOG_LEVEL: Optional[str] = "INFO"
    XRAY_CONFIG_PATH: Optional[str] = "/etc/xray/config.json"
    XRAY_VLESS_INBOUND: Optional[str]
    XRAY_VLESS_PORT: Optional[str]
    def __init__(self, path='config.yaml'):
        self.path = path
        self.load()
    def load(self):
        with open(self.path, 'r', encoding='utf-8') as f:
            data = yaml.safe_load(f)
            if data is None:
                return
            self.XRAY_IP = data.get('xray_ip', self.XRAY_IP)
            self.XRAY_API_PORT = data.get('xray_api_port', self.XRAY_API_PORT)
            self.PARTNER_WS_HEALTH_CHECK_INTERVAL = data.get('partner_ws_health_check_interval', self.PARTNER_WS_HEALTH_CHECK_INTERVAL)
            self.PARTNER_TRAFFIC_COLLECT_INTERVAL = data.get('partner_traffic_collect_interval', self.PARTNER_TRAFFIC_COLLECT_INTERVAL)
            self.PARTNER_TRAFFIC_REPORT_INTERVAL = data.get('partner_traffic_report_interval', self.PARTNER_TRAFFIC_REPORT_INTERVAL)
            self.PARTNER_LOG_FILE_NAME = data.get('partner_log_file_name', self.PARTNER_LOG_FILE_NAME)
            self.PARTNER_LOG_LEVEL = data.get('partner_log_level', self.PARTNER_LOG_LEVEL)
            self.XRAY_CONFIG_PATH = data.get('xray_config_path', self.XRAY_CONFIG_PATH)

        with open(self.XRAY_CONFIG_PATH, "r", encoding="utf-8") as f:
            config = json.load(f)
            first_inbound = config["inbounds"][0]
            self.XRAY_VLESS_INBOUND = first_inbound.get("tag", "default")
            self.XRAY_VLESS_PORT = first_inbound.get("port", 0)
    def reload(self):
        self.load()

config = Config()