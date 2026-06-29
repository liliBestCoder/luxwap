#!/usr/bin/env python3
import paramiko

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)

def run(cmd):
    stdin, stdout, stderr = client.exec_command(cmd)
    out = stdout.read().decode().strip()
    err = stderr.read().decode().strip()
    return out or err

print("=== Java ===")
print(run("java -version 2>&1"))
print("\n=== MySQL ===")
print(run("mysql --version 2>&1"))
print("\n=== MySQL Status ===")
print(run("systemctl is-active mysql"))
print("\n=== Database ===")
print(run("mysql -u root -p123456 -e 'SHOW DATABASES;' 2>&1"))
print("\n=== /opt/ruoyi ===")
print(run("ls -la /opt/ruoyi/"))
print("\n=== /home/ruoyi/uploadPath ===")
print(run("ls -la /home/ruoyi/uploadPath/"))

client.close()
