#!/usr/bin/env python3
import paramiko
import subprocess
import os
import time

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"
dump_file = r"D:\RuoYi\xray_partner_full.sql"

# 1. Dump local database - simple dump with no extras
print("=== Step 1: Dumping local xray_partner ===")
cmd = f'"D:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe" -u root -p123456 --compact --no-tablespaces xray_partner > "{dump_file}" 2>nul'
subprocess.run(cmd, shell=True, timeout=300)

size = os.path.getsize(dump_file)
print(f"Dump size: {size/1024:.1f} KB, {size/1024/1024:.2f} MB")

# Show first few lines to confirm it's valid
with open(dump_file, 'r', encoding='utf-8', errors='replace') as f:
    first_lines = f.read(500)
print(f"Dump starts with: {first_lines[:200]}...")

# 2. Connect to server
client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)

def run(cmd, timeout=300):
    stdin, stdout, stderr = client.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8', errors='replace').strip()
    err = stderr.read().decode('utf-8', errors='replace').strip()
    return exit_code, out, err

# 3. Stop app
print("\n=== Step 2: Stop app + reset DB ===")
run("pkill -f ruoyi-admin 2>/dev/null; sleep 2", timeout=10)

# 4. Recreate database
run("mysql -u root -p123456 -e \"DROP DATABASE IF EXISTS xray_partner;\" 2>/dev/null", timeout=30)
run("mysql -u root -p123456 -e \"CREATE DATABASE xray_partner CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\" 2>/dev/null", timeout=30)

# 5. Upload via SFTP
print("=== Step 3: Upload dump to server ===")
sftp = client.open_sftp()
sftp.put(dump_file, "/opt/ruoyi/xray_partner_full.sql")
sftp.close()
print("Upload done")

# 6. Import
print("=== Step 4: Import database ===")
exit_code, out, err = run("mysql -u root -p123456 xray_partner < /opt/ruoyi/xray_partner_full.sql 2>&1", timeout=600)
print(f"Import exit: {exit_code}")
if err and 'Warning' not in err:
    print(f"Error: {err[:500]}")
if out:
    print(f"Output: {out[:500]}")

# 7. Verify
print("\n=== Step 5: Verify ===")
_, out, _ = run("mysql -u root -p123456 xray_partner -e 'SHOW TABLES;' 2>&1 | grep -v Warning", timeout=10)
tables = [t.strip() for t in out.split('\n') if t.strip()]
print(f"Tables count: {len(tables)}")
if len(tables) > 5:
    print(f"Tables: {', '.join(tables[:10])}... ({len(tables)} total)")
else:
    print(f"Tables: {tables}")

# 8. Check xray_user specifically
_, out, _ = run("mysql -u root -p123456 xray_partner -e 'SELECT COUNT(*) as user_count FROM xray_user;' 2>&1 | grep -v Warning", timeout=10)
print(f"Xray users: {out}")

# 9. Restart app
print("\n=== Step 6: Restart app ===")
run("nohup java -Xms512m -Xmx1024m -jar /opt/ruoyi/ruoyi-admin.jar --server.port=8000 > /opt/ruoyi/app.log 2>&1 &", timeout=5)

time.sleep(12)
_, out, _ = run("ss -tlnp | grep 8000", timeout=10)
print(f"Port 8000: {'Listening' if out else 'Not ready yet'}")

_, out, _ = run("curl -s -o /dev/null -w '%{http_code}' http://localhost:8000/", timeout=10)
print(f"HTTP status: {out}")

client.close()
print("\n=== All done ===")
