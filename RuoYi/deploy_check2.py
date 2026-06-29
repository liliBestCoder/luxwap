#!/usr/bin/env python3
import paramiko
import sys
import socket

host = "101.201.215.20"
port = 22
user = "root"
password = "N@qiushuiaixue"

print(f"Trying SSH {user}@{host}:{port}")
print(f"Password length: {len(password)}, chars: {[c for c in password]}")

try:
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    
    # First test raw socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(10)
    result = sock.connect_ex((host, port))
    print(f"Socket connect result: {result}")
    sock.close()
    
    if result != 0:
        print("Socket connection failed - host unreachable or port closed")
        sys.exit(1)
    
    print("Socket OK, trying SSH auth...")
    client.connect(
        host, port=port, 
        username=user, password=password, 
        timeout=15,
        allow_agent=False,
        look_for_keys=False,
        compress=True
    )
    print("Connected successfully!")
    
    stdin, stdout, stderr = client.exec_command("hostname")
    print(f"Hostname: {stdout.read().decode().strip()}")
    
    client.close()
    
except paramiko.AuthenticationException as e:
    print(f"Authentication failed: {e}")
    print("Possible issues: wrong password, SSH key auth required, or disabled password auth")
except paramiko.SSHException as e:
    print(f"SSH error: {e}")
except socket.timeout:
    print("Connection timeout")
except Exception as e:
    print(f"Error: {type(e).__name__}: {e}")
    sys.exit(1)
