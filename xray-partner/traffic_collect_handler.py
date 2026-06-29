import time
import json
import threading
import copy
from base_handler import BaseHandler
from config import config
from logger import logger
from xray_api_client import xray_api_client
from google.protobuf.json_format import MessageToDict

class TrafficCollectHandler(BaseHandler):
    def __init__(self, client):
        self.collect = -1
        self.client = client
        self.load_traffic_map()
        self.lock = threading.Lock()

    def on_open(self):
        if self.collect == -1:
            self.collect = self.start_collect()

    def save_traffic_map(self, traffic_map):
        try:
            with open('traffic_map.json', 'w') as f:
                json.dump(traffic_map, f)
            logger.info("Traffic map saved successfully.")
        except (IOError, json.JSONDecodeError) as e:
            logger.error(f"Error saving traffic map", e)

    def load_traffic_map(self):
        try:
            with open('traffic_map.json', 'r') as f:
                self.traffic_map = json.load(f)
        except FileNotFoundError:
            self.traffic_map = {}

    def update_traffic_map(self):
        with self.lock:
            response = xray_api_client.query_traffic("", reset=False)
            if response is None:
                return
            stats = [MessageToDict(stat) for stat in response]

            for stat in stats:
                name = stat['name']
                new_value = stat['value']

                if name in self.traffic_map:
                    old_value = self.traffic_map[name]['value']
                    delta = int(new_value or "0") - int(old_value or "0")
                    # 这里xray如果有重启过 new_value势必会小于old_value 这个时候取new_value为增量流量
                    if delta < 0:
                        delta = new_value
                    self.traffic_map[name]['delta'] += delta
                else:
                    self.traffic_map[name] = {'value': new_value, 'delta': 0}

                self.traffic_map[name]['value'] = new_value
    def start_collect(self):
        def collect():
            loop_cnt = 0
            while True:
                time.sleep(config.PARTNER_TRAFFIC_COLLECT_INTERVAL)
                self.update_traffic_map()
                loop_cnt += 1
                if loop_cnt % 6 == 0:
                    with self.lock:
                        traffic_map_copy = copy.deepcopy(self.traffic_map)
                    self.save_traffic_map(traffic_map_copy)
                    loop_cnt = 0

        threading.Thread(target=collect, daemon=True).start()

        def report():
            while True:
                time.sleep(config.PARTNER_TRAFFIC_REPORT_INTERVAL)
                with self.lock:
                    traffic_map_copy = copy.deepcopy(self.traffic_map)

                result = []
                for key, value in traffic_map_copy.items():
                    result.append({
                        "name": key,
                        "delta": value["delta"]
                    })

                self.client.send(json.dumps({
                    "type": "traffic_collect",
                    "data": result,
                    "time": time.time()
                }))

        threading.Thread(target=report, daemon=True).start()
        return 1
