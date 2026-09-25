import time
import json
import threading
import copy
import uuid
import os
import mmap
from base_handler import BaseHandler
from config import config
from logger import logger
from xray_api_client import xray_api_client
from google.protobuf.json_format import MessageToDict

class TrafficCollectHandler(BaseHandler):
    LINE_LEN = 128

    def __init__(self, client):
        self.collect = -1
        self.client = client
        self.traffic_file = 'traffic_map.txt'
        self.file_handle = None
        self.mmap_obj = None
        self.traffic_map = {}
        self.line_index = {}
        self.lock = threading.Lock()
        self.batch_lock = threading.Lock()
        self.current_batch = None
        self.init_mmap()

    def on_open(self):
        if self.collect == -1:
            self.collect = self.start_collect()

    def on_message(self, message):
        try:
            data = json.loads(message)
            if data.get("type") == "traffic_ack":
                batch_id = data.get("batch_id")
                seq = data.get("seq")
                with self.batch_lock:
                    if self.current_batch and self.current_batch.get("batch_id") == batch_id:
                        self.current_batch["acked_seqs"].add(seq)
                        while (self.current_batch["min_ack_seq"] + 1) in self.current_batch["acked_seqs"]:
                            self.current_batch["min_ack_seq"] += 1
                        logger.debug("[TrafficCollect] traffic_ack received: batch=%s, seq=%s, min_ack_seq=%s",
                                     batch_id, seq, self.current_batch["min_ack_seq"])
                        if self.current_batch["min_ack_seq"] >= self.current_batch["total_seq"]:
                            logger.info("[TrafficCollect] Batch %s successfully and completely ACKed by server.", batch_id)
                            self.current_batch = None
        except Exception as e:
            logger.error("[TrafficCollect] Failed to handle message: %s", e)

    @staticmethod
    def format_line_bytes(name, val, delta):
        name_bytes = str(name).encode('utf-8')[:85]
        name_field = name_bytes + b' ' * (85 - len(name_bytes))
        val_bytes = str(val).encode('utf-8')[:20]
        val_field = val_bytes + b' ' * (20 - len(val_bytes))
        delta_bytes = str(delta).encode('utf-8')[:20]
        delta_field = delta_bytes + b' ' * (20 - len(delta_bytes))
        return name_field + b'\t' + val_field + b'\t' + delta_field + b'\n'

    def init_mmap(self):
        with self.lock:
            # 1. 兼容迁移 legacy traffic_map.json
            if not os.path.exists(self.traffic_file) and os.path.exists('traffic_map.json'):
                try:
                    with open('traffic_map.json', 'r', encoding='utf-8') as f:
                        legacy_map = json.load(f)
                    with open(self.traffic_file, 'wb') as f:
                        for name, item in legacy_map.items():
                            line_bytes = self.format_line_bytes(name, item.get('value', 0), item.get('delta', 0))
                            f.write(line_bytes)
                    logger.info("[TrafficCollect] Migrated legacy traffic_map.json to mmap text format.")
                except Exception as e:
                    logger.error("[TrafficCollect] Failed to migrate traffic_map.json: %s", e)

            # 2. 如果文件不存在，创建空文件
            if not os.path.exists(self.traffic_file):
                try:
                    with open(self.traffic_file, 'wb') as f:
                        pass
                except Exception as e:
                    logger.error("[TrafficCollect] Failed to create %s: %s", self.traffic_file, e)

            # 3. 检查是否有历史变长文件，自动转换为定长 128 字节
            if os.path.exists(self.traffic_file) and os.path.getsize(self.traffic_file) > 0:
                file_size = os.path.getsize(self.traffic_file)
                if file_size % self.LINE_LEN != 0:
                    logger.warning("[TrafficCollect] %s is not fixed-width format (size=%d). Converting...", self.traffic_file, file_size)
                    try:
                        records = []
                        with open(self.traffic_file, 'r', encoding='utf-8', errors='ignore') as f:
                            for line in f:
                                line = line.strip()
                                if not line:
                                    continue
                                parts = line.split('\t')
                                if len(parts) >= 3:
                                    records.append((parts[0].strip(), parts[1].strip(), int(parts[2].strip() or "0")))
                        with open(self.traffic_file, 'wb') as f:
                            for name, val, delta in records:
                                f.write(self.format_line_bytes(name, val, delta))
                        logger.info("[TrafficCollect] Successfully converted %d records to fixed-width format.", len(records))
                    except Exception as e:
                        logger.error("[TrafficCollect] Failed to convert traffic_map.txt: %s", e)

            # 4. 解析定长行，建立内存索引和 traffic_map
            if os.path.exists(self.traffic_file) and os.path.getsize(self.traffic_file) > 0:
                try:
                    with open(self.traffic_file, 'rb') as f:
                        idx = 0
                        while True:
                            chunk = f.read(self.LINE_LEN)
                            if not chunk or len(chunk) < self.LINE_LEN:
                                break
                            try:
                                line_str = chunk.decode('utf-8', errors='ignore').rstrip('\r\n')
                                parts = line_str.split('\t')
                                if len(parts) >= 3:
                                    name = parts[0].strip()
                                    val = parts[1].strip()
                                    delta = int(parts[2].strip() or "0")
                                    self.traffic_map[name] = {'value': val, 'delta': delta}
                                    self.line_index[name] = idx
                            except Exception as e:
                                logger.warning("[TrafficCollect] Line parse warning at index %d: %s", idx, e)
                            idx += 1

                    self.file_handle = open(self.traffic_file, 'r+b')
                    self.mmap_obj = mmap.mmap(self.file_handle.fileno(), 0)
                    logger.info("[TrafficCollect] Loaded %d traffic records via mmap.", len(self.line_index))
                except Exception as e:
                    logger.error("[TrafficCollect] Error initializing mmap from %s: %s", self.traffic_file, e)

    def _append_new_entries(self, entries):
        try:
            if self.mmap_obj:
                self.mmap_obj.close()
                self.mmap_obj = None
            if self.file_handle:
                self.file_handle.close()
                self.file_handle = None

            current_idx = len(self.line_index)
            with open(self.traffic_file, 'ab') as f:
                for name, val, delta in entries:
                    line_bytes = self.format_line_bytes(name, val, delta)
                    f.write(line_bytes)
                    self.line_index[name] = current_idx
                    current_idx += 1
                f.flush()

            self.file_handle = open(self.traffic_file, 'r+b')
            self.mmap_obj = mmap.mmap(self.file_handle.fileno(), 0)
        except Exception as e:
            logger.error("[TrafficCollect] Error appending new entries to mmap: %s", e)

    def flush_traffic_map(self):
        try:
            if self.mmap_obj:
                self.mmap_obj.flush()
                logger.debug("[TrafficCollect] Traffic mmap flushed successfully.")
        except Exception as e:
            logger.error("[TrafficCollect] Error flushing mmap: %s", e)

    def update_traffic_map(self):
        with self.lock:
            response = xray_api_client.query_traffic("", reset=False)
            if response is None:
                return
            stats = [MessageToDict(stat) for stat in response]

            new_entries = []
            for stat in stats:
                name = stat['name']
                new_value = stat['value']

                if name in self.traffic_map:
                    old_value = self.traffic_map[name]['value']
                    delta = int(new_value or "0") - int(old_value or "0")
                    # 这里xray如果有重启过 new_value势必会小于old_value 这个时候取new_value为增量流量
                    if delta < 0:
                        delta = int(new_value or "0")
                    self.traffic_map[name]['delta'] += delta
                    self.traffic_map[name]['value'] = new_value

                    # 原地修改内存映射 mmap (O(1) 零磁盘IO)
                    if self.mmap_obj and name in self.line_index:
                        idx = self.line_index[name]
                        offset = idx * self.LINE_LEN
                        line_bytes = self.format_line_bytes(name, new_value, self.traffic_map[name]['delta'])
                        try:
                            self.mmap_obj[offset:offset + self.LINE_LEN] = line_bytes
                        except Exception as e:
                            logger.error("[TrafficCollect] Error updating mmap at offset %d: %s", offset, e)
                else:
                    self.traffic_map[name] = {'value': new_value, 'delta': 0}
                    new_entries.append((name, new_value, 0))

            if new_entries:
                self._append_new_entries(new_entries)

    def start_collect(self):
        def collect():
            loop_cnt = 0
            while True:
                time.sleep(config.PARTNER_TRAFFIC_COLLECT_INTERVAL)
                self.update_traffic_map()
                loop_cnt += 1
                if loop_cnt % 6 == 0:
                    with self.lock:
                        self.flush_traffic_map()
                    loop_cnt = 0

        threading.Thread(target=collect, daemon=True).start()

        def report():
            while True:
                time.sleep(config.PARTNER_TRAFFIC_REPORT_INTERVAL)
                with self.lock:
                    if self.mmap_obj and len(self.mmap_obj) >= self.LINE_LEN:
                        raw_bytes = bytes(self.mmap_obj[:])
                        total_records = len(raw_bytes) // self.LINE_LEN
                    else:
                        raw_bytes = b""
                        total_records = 0

                chunk_size = 100
                total_seq = (total_records + chunk_size - 1) // chunk_size if total_records > 0 else 1
                batch_id = str(uuid.uuid4())
                chunks = {}
                now_time = time.time()
                for i in range(total_seq):
                    seq = i + 1
                    start_record = i * chunk_size
                    end_record = min((i + 1) * chunk_size, total_records)
                    chunk_slice = raw_bytes[start_record * self.LINE_LEN : end_record * self.LINE_LEN]

                    chunk_data = []
                    for b_offset in range(0, len(chunk_slice), self.LINE_LEN):
                        block = chunk_slice[b_offset : b_offset + self.LINE_LEN]
                        name = block[0:85].decode('utf-8', errors='ignore').rstrip()
                        delta_str = block[107:127].decode('utf-8', errors='ignore').rstrip()
                        try:
                            delta = int(delta_str or "0")
                        except ValueError:
                            delta = 0
                        chunk_data.append({
                            "name": name,
                            "delta": delta
                        })

                    chunks[seq] = json.dumps({
                        "type": "traffic_collect",
                        "batch_id": batch_id,
                        "seq": seq,
                        "total_seq": total_seq,
                        "data": chunk_data,
                        "time": now_time
                    })

                with self.batch_lock:
                    self.current_batch = {
                        "batch_id": batch_id,
                        "total_seq": total_seq,
                        "chunks": chunks,
                        "min_ack_seq": 0,
                        "acked_seqs": set(),
                        "last_send_time": now_time,
                        "retries": 0
                    }

                for seq in range(1, total_seq + 1):
                    self.client.send(chunks[seq])

        def retransmit_checker():
            while True:
                time.sleep(2)
                with self.batch_lock:
                    if self.current_batch:
                        now = time.time()
                        if now - self.current_batch["last_send_time"] >= 2.0:
                            if self.current_batch["retries"] < 5:
                                self.current_batch["retries"] += 1
                                self.current_batch["last_send_time"] = now
                                start_seq = self.current_batch["min_ack_seq"] + 1
                                logger.warning("[TrafficCollect] Resending unacked traffic chunks for batch %s from seq %d to %d (retry %d)...",
                                               self.current_batch["batch_id"], start_seq, self.current_batch["total_seq"], self.current_batch["retries"])
                                for s in range(start_seq, self.current_batch["total_seq"] + 1):
                                    if s not in self.current_batch["acked_seqs"]:
                                        chunk_payload = self.current_batch["chunks"].get(s)
                                        if chunk_payload:
                                            self.client.send(chunk_payload)
                            else:
                                logger.error("[TrafficCollect] Traffic batch %s failed after 5 retries, discarding.", self.current_batch["batch_id"])
                                self.current_batch = None

        threading.Thread(target=report, daemon=True).start()
        threading.Thread(target=retransmit_checker, daemon=True).start()
        return 1
