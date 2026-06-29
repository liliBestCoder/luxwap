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

# Check users
print("=== 所有用户 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT user_id, user_name, nick_name, status, role_id FROM sys_user;' 2>&1 | grep -v Warning"))

print("\n=== 所有角色 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT role_id, role_name, role_key, status FROM sys_role;' 2>&1 | grep -v Warning"))

print("\n=== 用户-角色关联 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT * FROM sys_user_role;' 2>&1 | grep -v Warning"))

print("\n=== 角色的菜单权限数 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT r.role_id, r.role_name, r.role_key, COUNT(srm.menu_id) as menu_count FROM sys_role r LEFT JOIN sys_role_menu srm ON r.role_id = srm.role_id GROUP BY r.role_id, r.role_name, r.role_key ORDER BY menu_count DESC;' 2>&1 | grep -v Warning"))

print("\n=== 所有菜单总数 ===")
print(run("mysql -u root -p123456 xray_partner -e 'SELECT COUNT(*) as total_menus FROM sys_menu;' 2>&1 | grep -v Warning"))

client.close()
