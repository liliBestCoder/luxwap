#!/usr/bin/env python3
import paramiko

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)

cmds = [
    ("MySQL status", "systemctl status mysql 2>&1 || systemctl status mysqld 2>&1 || echo 'MySQL not installed'"),
    ("MariaDB", "systemctl status mariadb 2>&1 || echo 'no mariadb'"),
    ("Port 3306", "ss -tlnp | grep 3306 || echo 'nothing on 3306'"),
    ("Available packages", "apt list --installed 2>/dev/null | grep -i mysql || echo 'no mysql packages'"),
]

for label, cmd in cmds:
    stdin, stdout, stderr = client.exec_command(cmd)
    out = stdout.read().decode().strip()
    err = stderr.read().decode().strip()
    print(f"--- {label} ---")
    print(out if out else err if err else "(empty)")

client.close()
