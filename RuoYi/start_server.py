#!/usr/bin/env python3
"""Write startup script and start the app on the server"""
import paramiko

host = "101.201.215.20"
user = "root"
password = "N@qiushuiaixue2"

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(host, username=user, password=password, timeout=15)

def run(cmd, timeout=30):
    stdin, stdout, stderr = client.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode().strip()
    err = stderr.read().decode().strip()
    print(out if out else err)
    return exit_code, out, err

# Write startup script
startup_script = """#!/bin/bash
AppName=ruoyi-admin.jar
APP_HOME=/opt/ruoyi
JVM_OPTS="-Dname=$AppName -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m"

cd $APP_HOME

case "$1" in
  start)
    echo "Starting $AppName on port 8000..."
    nohup java $JVM_OPTS -jar $AppName --server.port=8000 > /opt/ruoyi/app.log 2>&1 &
    echo "PID: $!"
    ;;
  stop)
    echo "Stopping $AppName..."
    PID=$(ps aux | grep "$AppName" | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
      kill -15 $PID
      echo "Stopped PID $PID"
    else
      echo "Not running"
    fi
    ;;
  restart)
    $0 stop
    sleep 2
    $0 start
    ;;
  status)
    PID=$(ps aux | grep "$AppName" | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
      echo "Running (PID: $PID)"
    else
      echo "Not running"
    fi
    ;;
  logs)
    tail -f /opt/ruoyi/app.log
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|logs}"
    ;;
esac
"""

# Write script via remote command (heredoc)
print("=== Writing startup script ===")
# Use Python to write the file remotely via sftp
sftp = client.open_sftp()
with sftp.open("/opt/ruoyi/ruoyi.sh", "w") as f:
    f.write(startup_script)
sftp.close()

run("chmod +x /opt/ruoyi/ruoyi.sh")
print("Startup script written.")

# Start the app
print("\n=== Starting application ===")
run("nohup java -Xms512m -Xmx1024m -jar /opt/ruoyi/ruoyi-admin.jar --server.port=8000 > /opt/ruoyi/app.log 2>&1 &", timeout=5)

# Wait a bit and check
import time
time.sleep(8)

print("\n=== Check process ===")
run("ps aux | grep ruoyi-admin | grep -v grep || echo 'Not running'")

print("\n=== Check port ===")
run("ss -tlnp | grep 8000 || echo 'Port 8000 not listening'")

print("\n=== Last 30 lines of app.log ===")
run("tail -30 /opt/ruoyi/app.log")

client.close()
print("\n=== Done ===")
