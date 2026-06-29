#!/usr/bin/env python3
import paramiko

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
    return out or err

print("=== 用户详情 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT user_id, login_name, user_name, status FROM sys_user;' 2>&1 | grep -v Warning"))

print("\n=== 角色详情 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT role_id, role_name, role_key, status FROM sys_role;' 2>&1 | grep -v Warning"))

print("\n=== 每个用户关联的角色 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT u.user_id, u.login_name, u.user_name, r.role_name, r.role_key FROM sys_user u JOIN sys_user_role ur ON u.user_id = ur.user_id JOIN sys_role r ON ur.role_id = r.role_id;' 2>&1 | grep -v Warning"))

client.close()
