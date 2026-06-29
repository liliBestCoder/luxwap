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

print("=== 所有用户 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT user_id, user_name, nick_name, status FROM sys_user;' 2>&1 | grep -v Warning"))

print("\n=== 角色1(超级管理员)的菜单权限 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT count(*) as menu_count FROM sys_role_menu WHERE role_id=1;' 2>&1 | grep -v Warning"))

print("\n=== 角色2(普通角色)的菜单权限 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT count(*) as menu_count FROM sys_role_menu WHERE role_id=2;' 2>&1 | grep -v Warning"))

client.close()
