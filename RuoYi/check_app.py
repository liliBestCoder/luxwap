#!/usr/bin/env python3
import paramiko
import time

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)

def run(cmd, timeout=10):
    stdin, stdout, stderr = client.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode().strip()
    err = stderr.read().decode().strip()
    print(out if out else err)
    return out, err

# Wait longer
print("Waiting 15s for Spring Boot to finish starting...")
time.sleep(15)

print("\n=== Process status ===")
run("ps aux | grep ruoyi-admin | grep -v grep | awk '{print $2, $8, $9}'")

print("\n=== Port 8000 ===")
run("ss -tlnp | grep 8000")

print("\n=== Last 50 lines of log ===")
run("tail -50 /opt/ruoyi/app.log")

print("\n=== Curl test ===")
out, _ = run("curl -s -o /dev/null -w '%{http_code}' http://localhost:8000/ 2>&1")
print(f"HTTP status: {out}")

client.close()
