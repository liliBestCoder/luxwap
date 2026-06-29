#!/usr/bin/env python3
"""
Phase 1: Install Java 17 + MySQL 8 on remote Ubuntu server
"""
import paramiko
import time
import os

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)

def run(cmd, timeout=30):
    print(f"\n$ {cmd}")
    stdin, stdout, stderr = client.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode().strip()
    err = stderr.read().decode().strip()
    if out: print(out)
    if err: print(f"[stderr] {err}")
    return exit_code, out, err

# 1. Update apt
print("=== Updating apt ===")
run("apt update -y", timeout=60)

# 2. Install JDK 17
print("\n=== Installing JDK 17 ===")
run("apt install -y openjdk-17-jdk", timeout=120)

# 3. Verify Java
run("java -version")

# 4. Install MySQL 8
print("\n=== Installing MySQL 8 ===")
run("apt install -y mysql-server", timeout=120)

# 5. Check MySQL status
run("systemctl status mysql --no-pager | head -10")

# 6. Create /opt/ruoyi dir
run("mkdir -p /opt/ruoyi /home/ruoyi/uploadPath")

# 7. Install unzip
run("apt install -y unzip", timeout=30)

# 8. Create database
print("\n=== Creating database ===")
run("mysql -u root -e \"CREATE DATABASE IF NOT EXISTS xray_partner CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\" 2>&1", timeout=15)
run("mysql -u root -e \"ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456'; FLUSH PRIVILEGES;\" 2>&1", timeout=15)

client.close()
print("\n=== Phase 1 complete ===")
