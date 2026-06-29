#!/usr/bin/env python3
"""Upload JAR + SQL files to server and import database"""
import paramiko
import os
import time

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"
local_base = r"D:\RuoYi"
remote_base = "/opt/ruoyi"

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)
sftp = client.open_sftp()

files_to_upload = [
    (r"ruoyi-admin\target\ruoyi-admin.jar", "ruoyi-admin.jar"),
    (r"sql\ry_20250416.sql", "ry_20250416.sql"),
    (r"sql\quartz.sql", "quartz.sql"),
]

for local_rel, remote_name in files_to_upload:
    local_path = os.path.join(local_base, local_rel)
    remote_path = f"{remote_base}/{remote_name}"
    file_size = os.path.getsize(local_path)
    print(f"Uploading {remote_name} ({file_size/1024/1024:.1f} MB)...")
    start = time.time()
    sftp.put(local_path, remote_path)
    elapsed = time.time() - start
    print(f"  Done in {elapsed:.1f}s")

sftp.close()

def run(cmd, timeout=120):
    stdin, stdout, stderr = client.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode().strip()
    err = stderr.read().decode().strip()
    if out: print(out)
    if err and exit_code != 0: print(f"[err] {err}")
    return exit_code

# Verify files
print("\n=== Files on server ===")
run("ls -lh /opt/ruoyi/")

# Import SQL - quartz first, then main data
print("\n=== Importing quartz.sql ===")
run(f"mysql -u root -p123456 xray_partner < {remote_base}/quartz.sql 2>&1")
time.sleep(1)

print("\n=== Importing ry_20250416.sql ===")
run(f"mysql -u root -p123456 xray_partner < {remote_base}/ry_20250416.sql 2>&1")
time.sleep(1)

# Verify tables
print("\n=== Tables created ===")
run("mysql -u root -p123456 xray_partner -e 'SHOW TABLES;' 2>&1")

client.close()
print("\n=== Upload + DB import complete ===")
