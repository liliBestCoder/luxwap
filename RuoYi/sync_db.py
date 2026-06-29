#!/usr/bin/env python3
"""
Export local xray_partner database and import to remote server
"""
import paramiko
import subprocess
import os
import time

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"
dump_file = r"D:\RuoYi\xray_partner_full.sql"

# 1. Dump local database
print("=== Dumping local xray_partner database ===")
dump_cmd = f'"D:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe" -u root -p123456 --routines --triggers --events xray_partner > "{dump_file}"'
print(f"Running: mysqldump xray_partner ...")
result = subprocess.run(dump_cmd, shell=True, timeout=300)
if result.returncode != 0:
    print(f"mysqldump failed with code {result.returncode}")
    exit(1)

file_size = os.path.getsize(dump_file)
print(f"Dump file size: {file_size/1024/1024:.1f} MB")

# 2. Upload dump file to server
print("\n=== Uploading to remote server ===")
client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)
sftp = client.open_sftp()

remote_path = "/opt/ruoyi/xray_partner_full.sql"
start = time.time()
sftp.put(dump_file, remote_path)
elapsed = time.time() - start
print(f"Uploaded in {elapsed:.1f}s")
sftp.close()

# 3. Import to remote MySQL
print("\n=== Stopping application ===")
stdin, stdout, stderr = client.exec_command("ps aux | grep ruoyi-admin | grep -v grep | awk '{print $2}' | xargs -r kill -15", timeout=10)
time.sleep(3)

print("=== Dropping and recreating database ===")
client.exec_command("mysql -u root -p123456 -e 'DROP DATABASE IF EXISTS xray_partner; CREATE DATABASE xray_partner CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;'", timeout=30)

print("=== Importing full database dump ===")
stdin, stdout, stderr = client.exec_command(f"mysql -u root -p123456 xray_partner < /opt/ruoyi/xray_partner_full.sql", timeout=600)
exit_code = stdout.channel.recv_exit_status()
out = stdout.read().decode().strip()
err = stderr.read().decode().strip()
if exit_code != 0:
    print(f"Import failed: {err}")
    # Try without warnings
    client.exec_command(f"mysql -u root -p123456 xray_partner < /opt/ruoyi/xray_partner_full.sql 2>/dev/null", timeout=600)
else:
    print("Import successful!")

# 4. Verify
print("\n=== Verifying tables ===")
stdin, stdout, stderr = client.exec_command("mysql -u root -p123456 xray_partner -e 'SELECT COUNT(*) as total_tables FROM information_schema.tables WHERE table_schema=\"xray_partner\";' 2>&1 | grep -v Warning")
print(stdout.read().decode().strip())

# 5. Restart app
print("\n=== Restarting application ===")
client.exec_command("nohup java -Xms512m -Xmx1024m -jar /opt/ruoyi/ruoyi-admin.jar --server.port=8000 > /opt/ruoyi/app.log 2>&1 &", timeout=5)

time.sleep(10)
stdin, stdout, stderr = client.exec_command("ss -tlnp | grep 8000 || echo 'Still starting...'", timeout=10)
port_status = stdout.read().decode().strip()
print(f"Port 8000: {port_status}")

client.close()
print("\n=== Done ===")
