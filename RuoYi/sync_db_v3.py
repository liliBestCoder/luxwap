#!/usr/bin/env python3
import paramiko
import subprocess
import os
import time

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"
dump_file = r"D:\RuoYi\xray_partner_full.sql"

# 1. Dump local database - FULL dump (includes FK_CHECKS=0)
print("=== Dumping local xray_partner ===")
cmd = ('"D:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe" '
       '-u root -p123456 '
       '--no-tablespaces '
       '--routines '
       '--triggers '
       '--events '
       'xray_partner > "{0}" 2>nul'.format(dump_file))
subprocess.run(cmd, shell=True, timeout=300)

size = os.path.getsize(dump_file)
print(f"Dump size: {size/1024:.1f} KB, {size/1024/1024:.2f} MB")

# Check if SET FOREIGN_KEY_CHECKS is in the dump
with open(dump_file, 'r', encoding='utf-8', errors='replace') as f:
    content = f.read()

if 'FOREIGN_KEY_CHECKS' in content:
    print("✅ FOREIGN_KEY_CHECKS=0 is in the dump (good!)")
else:
    print("⚠️  FOREIGN_KEY_CHECKS not found, adding it")
    # Prepend SET FOREIGN_KEY_CHECKS=0
    content = "SET FOREIGN_KEY_CHECKS=0;\n" + content
    with open(dump_file, 'w', encoding='utf-8') as f:
        f.write(content)

line_count = content.count('\n')
print(f"Total lines: {line_count}")

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

# 3. Stop app + reset DB
print("\n=== Stopping app ===")
run("pkill -f ruoyi-admin 2>/dev/null; sleep 2", timeout=10)

print("=== Dropping & recreating database ===")
run("mysql -u root -p123456 -e \"DROP DATABASE IF EXISTS xray_partner; CREATE DATABASE xray_partner CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\" 2>/dev/null", timeout=30)

# 4. Upload
print("=== Uploading dump file ===")
sftp = client.open_sftp()
sftp.put(dump_file, "/opt/ruoyi/xray_partner_full.sql")
sftp.close()
print("Upload done")

# 5. Import
print("=== Importing database (this may take a while) ===")
exit_code, out, err = run("mysql -u root -p123456 xray_partner < /opt/ruoyi/xray_partner_full.sql 2>&1", timeout=600)
if exit_code != 0:
    # Filter out warnings
    real_errors = [l for l in err.split('\n') if l and 'Warning' not in l]
    if real_errors:
        print(f"Errors: {real_errors[:5]}")
    else:
        print("Import completed (with warnings only)")
else:
    print("Import successful!")

# 6. Verify
print("\n=== Verification ===")
_, out, _ = run("mysql -u root -p123456 xray_partner -e 'SELECT COUNT(*) as total_tables FROM information_schema.tables WHERE table_schema=\"xray_partner\";' 2>&1 | grep -v Warning", timeout=10)
print(f"Total tables: {out}")

_, out, _ = run("mysql -u root -p123456 xray_partner -e 'SELECT COUNT(*) as xray_users FROM xray_user;' 2>&1 | grep -v Warning", timeout=10)
print(f"Xray users: {out}")

_, out, _ = run("mysql -u root -p123456 xray_partner -e 'SHOW TABLES;' 2>&1 | grep -v Tables | grep -v Warning", timeout=10)
tables = [t.strip() for t in out.split('\n') if t.strip()]
print(f"All tables ({len(tables)}): {', '.join(tables)}")

# 7. Restart app
print("\n=== Restarting application ===")
run("nohup java -Xms512m -Xmx1024m -Duser.timezone=Asia/Shanghai -jar /opt/ruoyi/ruoyi-admin.jar --server.port=8000 > /opt/ruoyi/app.log 2>&1 &", timeout=5)

print("Waiting 15s for startup...")
time.sleep(15)

_, out, _ = run("ss -tlnp | grep 8000", timeout=10)
print(f"Port 8000: {'✅ Listening' if out else '❌ Not listening'}")

_, out, _ = run("curl -s -o /dev/null -w '%{http_code}' http://localhost:8000/", timeout=10)
print(f"HTTP status: {out}")

_, out, _ = run("tail -5 /opt/ruoyi/app.log", timeout=10)
print(f"Latest log: {out[:200] if out else 'empty'}")

client.close()
print("\n✅ Complete!")
