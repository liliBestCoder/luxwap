#!/usr/bin/env python3
import paramiko
import sys

host = "101.201.215.20"
port = 22
user = "root"
password = "N@qiushuiaixue2"

try:
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    print(f"Connecting {user}@{host}:{port} ...")
    client.connect(
        host, port=port,
        username=user, password=password,
        timeout=15,
        allow_agent=False,
        look_for_keys=False
    )
    print("Connected!")

    # Check environment
    stdin, stdout, stderr = client.exec_command("hostname")
    print(f"Hostname: {stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("java -version 2>&1")
    print(f"Java: {stdout.read().decode().strip() + stderr.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("which java")
    print(f"Java path: {stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("cat /etc/os-release | head -5")
    print(f"OS:\n{stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("free -h")
    print(f"Memory:\n{stdout.read().decode().strip()}")

    stdin, stdout, stderr = client.exec_command("df -h /")
    print(f"Disk:\n{stdout.read().decode().strip()}")

    # Check /opt and create app dir
    stdin, stdout, stderr = client.exec_command("mkdir -p /opt/ruoyi && ls -la /opt/ruoyi")
    print(f"Deploy dir:\n{stdout.read().decode().strip()}")
    err = stderr.read().decode().strip()
    if err:
        print(f"  stderr: {err}")

    client.close()
    print("\nAll good! Ready for deployment.")
except Exception as e:
    print(f"ERROR: {type(e).__name__}: {e}")
    sys.exit(1)
