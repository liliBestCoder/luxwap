#!/usr/bin/env python3
import paramiko
import sys

host = "101.201.215.20"
port = 22
user = "root"
password = "N@qiushuiaixue"

try:
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    print(f"Connecting to {host}:{port}...")
    client.connect(host, port=port, username=user, password=password, timeout=15)
    print("Connected!")

    stdin, stdout, stderr = client.exec_command("hostname")
    print(f"Hostname: {stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("java -version 2>&1")
    java_ver = stdout.read().decode().strip() + stderr.read().decode().strip()
    print(f"Java: {java_ver}")

    stdin, stdout, stderr = client.exec_command("uname -m")
    print(f"Arch: {stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("free -h")
    print(f"Memory:\n{stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("df -h /")
    print(f"Disk:\n{stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("ls /opt/ 2>/dev/null || echo /opt not found")
    print(f"/opt: {stdout.read().decode().strip()}")

    client.close()
    print("\nConnection check done.")
except Exception as e:
    print(f"Error: {e}", file=sys.stderr)
    sys.exit(1)
